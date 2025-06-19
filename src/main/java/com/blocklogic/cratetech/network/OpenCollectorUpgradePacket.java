package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.screen.custom.CollectorUpgradeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenCollectorUpgradePacket(BlockPos cratePos) implements CustomPacketPayload {
    public static final Type<OpenCollectorUpgradePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "open_collector_upgrade"));

    public static final StreamCodec<FriendlyByteBuf, OpenCollectorUpgradePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, OpenCollectorUpgradePacket::cratePos,
                    OpenCollectorUpgradePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenCollectorUpgradePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    serverPlayer.openMenu(new SimpleMenuProvider(
                            (containerId, playerInventory, player) ->
                                    new CollectorUpgradeMenu(containerId, playerInventory, crateEntity),
                            crateEntity.getDisplayName()
                    ), buf -> buf.writeBlockPos(packet.cratePos));
                }
            }
        });
    }
}