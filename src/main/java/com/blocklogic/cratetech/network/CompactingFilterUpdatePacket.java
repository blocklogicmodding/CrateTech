package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record CompactingFilterUpdatePacket(BlockPos cratePos, List<Item> filterItems) implements CustomPacketPayload {
    public static final Type<CompactingFilterUpdatePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "compacting_filter_update"));

    public static final StreamCodec<FriendlyByteBuf, CompactingFilterUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CompactingFilterUpdatePacket::cratePos,
                    StreamCodec.of(CompactingFilterUpdatePacket::encodeItems, CompactingFilterUpdatePacket::decodeItems), CompactingFilterUpdatePacket::filterItems,
                    CompactingFilterUpdatePacket::new
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

    public static void handle(CompactingFilterUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    crateEntity.updateCompactingFilter(packet.filterItems);
                    CTNetworkHandler.sendToPlayer(serverPlayer, new SyncCompactingSettingsPacket(packet.cratePos, crateEntity.getCompactingSettings()));
                }
            }
        });
    }
}