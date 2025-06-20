package com.blocklogic.cratetech.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record CrateContents(
        NonNullList<ItemStack> items,
        CollectorSettings collectorSettings,
        HopperSettings hopperSettings,
        CompactingSettings compactingSettings,
        int crateSize
) {
    public static final CrateContents EMPTY = new CrateContents(
            NonNullList.create(),
            CollectorSettings.DEFAULT,
            HopperSettings.DEFAULT,
            CompactingSettings.DEFAULT,
            0
    );

    public static final Codec<CrateContents> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(ItemStack.OPTIONAL_CODEC).xmap(
                            list -> {
                                NonNullList<ItemStack> nonNullList = NonNullList.create();
                                nonNullList.addAll(list);
                                return nonNullList;
                            },
                            list -> list.stream().toList()
                    ).fieldOf("items").forGetter(CrateContents::items),
                    CollectorSettings.CODEC.fieldOf("collector_settings").forGetter(CrateContents::collectorSettings),
                    HopperSettings.CODEC.fieldOf("hopper_settings").forGetter(CrateContents::hopperSettings),
                    CompactingSettings.CODEC.fieldOf("compacting_settings").forGetter(CrateContents::compactingSettings),
                    Codec.INT.fieldOf("crate_size").forGetter(CrateContents::crateSize)
            ).apply(instance, CrateContents::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CrateContents> STREAM_CODEC =
            StreamCodec.of(CrateContents::encode, CrateContents::decode);

    private static void encode(RegistryFriendlyByteBuf buf, CrateContents contents) {
        buf.writeVarInt(contents.items.size());
        for (ItemStack item : contents.items) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, item);
        }
        CollectorSettings.STREAM_CODEC.encode(buf, contents.collectorSettings);
        HopperSettings.STREAM_CODEC.encode(buf, contents.hopperSettings);
        CompactingSettings.STREAM_CODEC.encode(buf, contents.compactingSettings);
        buf.writeVarInt(contents.crateSize);
    }

    private static CrateContents decode(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size; i++) {
            items.set(i, ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
        }

        CollectorSettings collectorSettings = CollectorSettings.STREAM_CODEC.decode(buf);
        HopperSettings hopperSettings = HopperSettings.STREAM_CODEC.decode(buf);
        CompactingSettings compactingSettings = CompactingSettings.STREAM_CODEC.decode(buf);
        int crateSize = buf.readVarInt();

        return new CrateContents(items, collectorSettings, hopperSettings, compactingSettings, crateSize);
    }

    public boolean isEmpty() {
        return items.isEmpty() || items.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean hasUpgrades() {
        return !collectorSettings.equals(CollectorSettings.DEFAULT) ||
                !hopperSettings.equals(HopperSettings.DEFAULT) ||
                !compactingSettings.equals(CompactingSettings.DEFAULT);
    }

    public static CrateContents fromCrate(NonNullList<ItemStack> items, CollectorSettings collectorSettings,
                                          HopperSettings hopperSettings, CompactingSettings compactingSettings,
                                          int crateSize) {
        return new CrateContents(items, collectorSettings, hopperSettings, compactingSettings, crateSize);
    }
}