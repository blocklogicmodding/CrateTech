package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.component.CTDataComponents;
import com.blocklogic.cratetech.component.ItemFilterSettings;
import com.blocklogic.cratetech.screen.custom.ItemFilterMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncItemFilterSettingsPacket(ItemFilterSettings settings) implements CustomPacketPayload {
    public static final Type<SyncItemFilterSettingsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "sync_item_filter_settings"));

    public static final StreamCodec<FriendlyByteBuf, SyncItemFilterSettingsPacket> STREAM_CODEC =
            StreamCodec.composite(
                    StreamCodec.of(SyncItemFilterSettingsPacket::encodeSettings, SyncItemFilterSettingsPacket::decodeSettings), SyncItemFilterSettingsPacket::settings,
                    SyncItemFilterSettingsPacket::new
            );

    private static void encodeSettings(FriendlyByteBuf buf, ItemFilterSettings settings) {
        buf.writeVarInt(settings.filterItems().size());
        for (net.minecraft.world.item.Item item : settings.filterItems()) {
            ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
            buf.writeResourceLocation(itemId);
        }
        buf.writeBoolean(settings.whitelistMode());
        buf.writeBoolean(settings.matchTags());
    }

    private static ItemFilterSettings decodeSettings(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        java.util.List<net.minecraft.world.item.Item> items = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation itemId = buf.readResourceLocation();
            net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemId);
            if (item != null) {
                items.add(item);
            }
        }
        return new ItemFilterSettings(
                items,
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncItemFilterSettingsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                Player player = Minecraft.getInstance().player;
                if (player != null && player.containerMenu instanceof ItemFilterMenu filterMenu) {
                    // Find and update the filter item stack
                    ItemStack filterStack = findItemFilterInInventory(player);
                    if (!filterStack.isEmpty()) {
                        filterStack.set(CTDataComponents.ITEM_FILTER_SETTINGS.get(), packet.settings);
                    }
                }
            }
        });
    }

    private static ItemStack findItemFilterInInventory(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == com.blocklogic.cratetech.item.CTItems.ITEM_FILTER.get()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}