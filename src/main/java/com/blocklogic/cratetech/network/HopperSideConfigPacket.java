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

public record HopperSideConfigPacket(BlockPos cratePos, int direction) implements CustomPacketPayload {
    public static final Type<HopperSideConfigPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "hopper_side_config"));

    public static final StreamCodec<FriendlyByteBuf, HopperSideConfigPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, HopperSideConfigPacket::cratePos,
                    StreamCodec.of(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt), HopperSideConfigPacket::direction,
                    HopperSideConfigPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HopperSideConfigPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    crateEntity.toggleHopperSide(packet.direction);
                    CTNetworkHandler.sendToPlayer(serverPlayer, new SyncHopperSettingsPacket(packet.cratePos, crateEntity.getHopperSettings()));
                }
            }
        });
    }
}