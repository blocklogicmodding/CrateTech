package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CollectorWireframePacket(BlockPos cratePos) implements CustomPacketPayload {
    public static final Type<CollectorWireframePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "collector_wireframe"));

    public static final StreamCodec<FriendlyByteBuf, CollectorWireframePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CollectorWireframePacket::cratePos,
                    CollectorWireframePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CollectorWireframePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    crateEntity.toggleWireframe();
                    CTNetworkHandler.sendToPlayer(serverPlayer, new SyncCollectorSettingsPacket(packet.cratePos, crateEntity.getCollectorSettings()));
                }
            }
        });
    }
}