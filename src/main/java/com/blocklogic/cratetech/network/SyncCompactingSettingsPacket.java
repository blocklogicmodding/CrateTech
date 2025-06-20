package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.component.CompactingSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncCompactingSettingsPacket(BlockPos cratePos, CompactingSettings settings) implements CustomPacketPayload {
    public static final Type<SyncCompactingSettingsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("cratetech", "sync_compacting_settings"));

    public static final StreamCodec<FriendlyByteBuf, SyncCompactingSettingsPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SyncCompactingSettingsPacket::cratePos,
                    StreamCodec.of(SyncCompactingSettingsPacket::encodeSettings, SyncCompactingSettingsPacket::decodeSettings), SyncCompactingSettingsPacket::settings,
                    SyncCompactingSettingsPacket::new
            );

    private static void encodeSettings(FriendlyByteBuf buf, CompactingSettings settings) {
        buf.writeVarInt(settings.filterItems().size());
        for (Item item : settings.filterItems()) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            buf.writeResourceLocation(itemId);
        }
        buf.writeBoolean(settings.whitelistMode());
        buf.writeBoolean(settings.use3x3Recipes());
    }

    private static CompactingSettings decodeSettings(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        java.util.List<Item> items = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation itemId = buf.readResourceLocation();
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item != null) {
                items.add(item);
            }
        }
        return new CompactingSettings(
                items,
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncCompactingSettingsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().isClientSide()) {
                BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(packet.cratePos);
                if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                    crateEntity.setCompactingSettings(packet.settings);
                }
            }
        });
    }
}