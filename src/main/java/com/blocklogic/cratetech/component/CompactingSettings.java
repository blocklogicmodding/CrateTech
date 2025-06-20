package com.blocklogic.cratetech.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record CompactingSettings(
        List<Item> filterItems,
        boolean whitelistMode,
        boolean use3x3Recipes
) {
    public static final CompactingSettings DEFAULT = new CompactingSettings(
            new ArrayList<>(), true, true
    );

    public static final Codec<CompactingSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(BuiltInRegistries.ITEM.byNameCodec()).fieldOf("filter_items").forGetter(CompactingSettings::filterItems),
                    Codec.BOOL.fieldOf("whitelist_mode").forGetter(CompactingSettings::whitelistMode),
                    Codec.BOOL.fieldOf("use_3x3_recipes").forGetter(CompactingSettings::use3x3Recipes)
            ).apply(instance, CompactingSettings::new)
    );

    public static final StreamCodec<FriendlyByteBuf, CompactingSettings> STREAM_CODEC =
            StreamCodec.of(CompactingSettings::encode, CompactingSettings::decode);

    private static void encode(FriendlyByteBuf buf, CompactingSettings settings) {
        buf.writeVarInt(settings.filterItems.size());
        for (Item item : settings.filterItems) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            buf.writeResourceLocation(itemId);
        }
        buf.writeBoolean(settings.whitelistMode);
        buf.writeBoolean(settings.use3x3Recipes);
    }

    private static CompactingSettings decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation itemId = buf.readResourceLocation();
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item != null) {
                items.add(item);
            }
        }
        return new CompactingSettings(
                items,
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public CompactingSettings withFilterItems(List<Item> items) {
        return new CompactingSettings(new ArrayList<>(items), whitelistMode, use3x3Recipes);
    }

    public CompactingSettings withWhitelistMode(boolean whitelist) {
        return new CompactingSettings(new ArrayList<>(filterItems), whitelist, use3x3Recipes);
    }

    public CompactingSettings withUse3x3Recipes(boolean use3x3) {
        return new CompactingSettings(new ArrayList<>(filterItems), whitelistMode, use3x3);
    }

    public boolean shouldCompactItem(ItemStack itemStack) {
        if (filterItems.isEmpty()) {
            return true;
        }

        boolean isInFilter = filterItems.contains(itemStack.getItem());
        return whitelistMode ? isInFilter : !isInFilter;
    }

    public CompactingSettings reset() {
        return new CompactingSettings(new ArrayList<>(), true, true);
    }
}