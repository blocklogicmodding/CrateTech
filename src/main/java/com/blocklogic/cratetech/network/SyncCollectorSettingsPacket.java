package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.component.CollectorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncCollectorSettingsPacket(BlockPos cratePos, CollectorSettings settings) implements CustomPacketPayload {
    public static final Type<SyncCollectorSettingsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "sync_collector_settings"));

    public static final StreamCodec<FriendlyByteBuf, SyncCollectorSettingsPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SyncCollectorSettingsPacket::cratePos,
                    StreamCodec.of(SyncCollectorSettingsPacket::encodeSettings, SyncCollectorSettingsPacket::decodeSettings), SyncCollectorSettingsPacket::settings,
                    SyncCollectorSettingsPacket::new
            );

    private static void encodeSettings(FriendlyByteBuf buf, CollectorSettings settings) {
        buf.writeVarInt(settings.downAdjustment());
        buf.writeVarInt(settings.upAdjustment());
        buf.writeVarInt(settings.northAdjustment());
        buf.writeVarInt(settings.southAdjustment());
        buf.writeVarInt(settings.westAdjustment());
        buf.writeVarInt(settings.eastAdjustment());
        buf.writeBoolean(settings.wireframeVisible());
    }

    private static CollectorSettings decodeSettings(FriendlyByteBuf buf) {
        return new CollectorSettings(
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readBoolean()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncCollectorSettingsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    crateEntity.setCollectorSettings(packet.settings);
                }
            }
        });
    }
}