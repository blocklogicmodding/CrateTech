package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.component.CTDataComponents;
import com.blocklogic.cratetech.component.ItemFilterSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record ItemFilterUpdatePacket(List<Item> filterItems) implements CustomPacketPayload {
    public static final Type<ItemFilterUpdatePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "item_filter_update"));

    public static final StreamCodec<FriendlyByteBuf, ItemFilterUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    StreamCodec.of(ItemFilterUpdatePacket::encodeItems, ItemFilterUpdatePacket::decodeItems), ItemFilterUpdatePacket::filterItems,
                    ItemFilterUpdatePacket::new
            );

    private static void encodeItems(FriendlyByteBuf buf, List<Item> items) {
        buf.writeVarInt(items.size());
        for (Item item : items) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            buf.writeResourceLocation(itemId);
        }
    }

    private static List<Item> decodeItems(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation itemId = buf.readResourceLocation();
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ItemFilterUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                // Find the item filter in player's inventory
                ItemStack filterStack = findItemFilterInInventory(serverPlayer);
                if (!filterStack.isEmpty()) {
                    ItemFilterSettings currentSettings = filterStack.get(CTDataComponents.ITEM_FILTER_SETTINGS.get());
                    if (currentSettings == null) {
                        currentSettings = ItemFilterSettings.DEFAULT;
                    }
                    ItemFilterSettings newSettings = currentSettings.withFilterItems(packet.filterItems);
                    filterStack.set(CTDataComponents.ITEM_FILTER_SETTINGS.get(), newSettings);
                    CTNetworkHandler.sendToPlayer(serverPlayer, new SyncItemFilterSettingsPacket(newSettings));
                    CTNetworkHandler.sendToPlayer(serverPlayer, new SyncItemFilterSettingsPacket(newSettings));
                }
            }
        });
    }

    private static ItemStack findItemFilterInInventory(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == com.blocklogic.cratetech.item.CTItems.ITEM_FILTER.get()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}