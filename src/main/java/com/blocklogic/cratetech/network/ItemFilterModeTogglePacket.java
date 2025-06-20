package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.component.CTDataComponents;
import com.blocklogic.cratetech.component.ItemFilterSettings;
import com.blocklogic.cratetech.item.CTItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ItemFilterModeTogglePacket(boolean isWhitelistToggle) implements CustomPacketPayload {
    public static final Type<ItemFilterModeTogglePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "item_filter_mode_toggle"));

    public static final StreamCodec<FriendlyByteBuf, ItemFilterModeTogglePacket> STREAM_CODEC =
            StreamCodec.composite(
                    StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean), ItemFilterModeTogglePacket::isWhitelistToggle,
                    ItemFilterModeTogglePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ItemFilterModeTogglePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                ItemStack filterStack = findItemFilterInInventory(serverPlayer);
                if (!filterStack.isEmpty()) {
                    ItemFilterSettings currentSettings = filterStack.get(CTDataComponents.ITEM_FILTER_SETTINGS.get());
                    if (currentSettings == null) {
                        currentSettings = ItemFilterSettings.DEFAULT;
                    }

                    ItemFilterSettings newSettings;
                    if (packet.isWhitelistToggle) {
                        newSettings = currentSettings.withWhitelistMode(!currentSettings.whitelistMode());
                    } else {
                        newSettings = currentSettings.withMatchTags(!currentSettings.matchTags());
                    }

                    filterStack.set(CTDataComponents.ITEM_FILTER_SETTINGS.get(), newSettings);
                }
            }
        });
    }

    private static ItemStack findItemFilterInInventory(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == CTItems.ITEM_FILTER.get()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}