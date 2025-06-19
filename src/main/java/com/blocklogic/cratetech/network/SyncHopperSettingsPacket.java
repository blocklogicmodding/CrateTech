package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.component.HopperSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncHopperSettingsPacket(BlockPos cratePos, HopperSettings settings) implements CustomPacketPayload {
    public static final Type<SyncHopperSettingsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "sync_hopper_settings"));

    public static final StreamCodec<FriendlyByteBuf, SyncHopperSettingsPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SyncHopperSettingsPacket::cratePos,
                    StreamCodec.of(SyncHopperSettingsPacket::encodeSettings, SyncHopperSettingsPacket::decodeSettings), SyncHopperSettingsPacket::settings,
                    SyncHopperSettingsPacket::new
            );

    private static void encodeSettings(FriendlyByteBuf buf, HopperSettings settings) {
        buf.writeEnum(settings.up());
        buf.writeEnum(settings.down());
        buf.writeEnum(settings.north());
        buf.writeEnum(settings.south());
        buf.writeEnum(settings.east());
        buf.writeEnum(settings.west());
        buf.writeBoolean(settings.pushMode());
        buf.writeBoolean(settings.pullMode());
    }

    private static HopperSettings decodeSettings(FriendlyByteBuf buf) {
        return new HopperSettings(
                buf.readEnum(HopperSettings.SideMode.class),
                buf.readEnum(HopperSettings.SideMode.class),
                buf.readEnum(HopperSettings.SideMode.class),
                buf.readEnum(HopperSettings.SideMode.class),
                buf.readEnum(HopperSettings.SideMode.class),
                buf.readEnum(HopperSettings.SideMode.class),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncHopperSettingsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    crateEntity.setHopperSettings(packet.settings);
                }
            }
        });
    }
}