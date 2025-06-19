package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.screen.custom.CompactingUpgradeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenCompactingUpgradePacket(BlockPos cratePos) implements CustomPacketPayload {
    public static final Type<OpenCompactingUpgradePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "open_compacting_upgrade"));

    public static final StreamCodec<FriendlyByteBuf, OpenCompactingUpgradePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, OpenCompactingUpgradePacket::cratePos,
                    OpenCompactingUpgradePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenCompactingUpgradePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = serverPlayer.level().getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    serverPlayer.openMenu(new SimpleMenuProvider(
                            (containerId, playerInventory, player) ->
                                    new CompactingUpgradeMenu(containerId, playerInventory, crateEntity),
                            crateEntity.getDisplayName()
                    ), buf -> buf.writeBlockPos(packet.cratePos));
                }
            }
        });
    }
}