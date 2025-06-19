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

public record HopperModeTogglePacket(BlockPos cratePos, boolean isPushMode) implements CustomPacketPayload {
    public static final Type<HopperModeTogglePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "hopper_mode_toggle"));

    public static final StreamCodec<FriendlyByteBuf, HopperModeTogglePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, HopperModeTogglePacket::cratePos,
                    StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean), HopperModeTogglePacket::isPushMode,
                    HopperModeTogglePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HopperModeTogglePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    if (packet.isPushMode) {
                        crateEntity.toggleHopperPushMode();
                    } else {
                        crateEntity.toggleHopperPullMode();
                    }
                    CTNetworkHandler.sendToPlayer(serverPlayer, new SyncHopperSettingsPacket(packet.cratePos, crateEntity.getHopperSettings()));
                }
            }
        });
    }
}