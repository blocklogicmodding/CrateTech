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

public record HopperResetPacket(BlockPos cratePos) implements CustomPacketPayload {
    public static final Type<HopperResetPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "hopper_reset"));

    public static final StreamCodec<FriendlyByteBuf, HopperResetPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, HopperResetPacket::cratePos,
                    HopperResetPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HopperResetPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    crateEntity.resetHopperSettings();
                    CTNetworkHandler.sendToPlayer(serverPlayer, new SyncHopperSettingsPacket(packet.cratePos, crateEntity.getHopperSettings()));
                }
            }
        });
    }
}