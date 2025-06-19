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

public record CompactingModeTogglePacket(BlockPos cratePos, boolean isWhitelistToggle) implements CustomPacketPayload {
    public static final Type<CompactingModeTogglePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "compacting_mode_toggle"));

    public static final StreamCodec<FriendlyByteBuf, CompactingModeTogglePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CompactingModeTogglePacket::cratePos,
                    StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean), CompactingModeTogglePacket::isWhitelistToggle,
                    CompactingModeTogglePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CompactingModeTogglePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    if (packet.isWhitelistToggle) {
                        crateEntity.toggleCompactingWhitelistMode();
                    } else {
                        crateEntity.toggleCompacting3x3Mode();
                    }
                    CTNetworkHandler.sendToPlayer(serverPlayer, new SyncCompactingSettingsPacket(packet.cratePos, crateEntity.getCompactingSettings()));
                }
            }
        });
    }
}