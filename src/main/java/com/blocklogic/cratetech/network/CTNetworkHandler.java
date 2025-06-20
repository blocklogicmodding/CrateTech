package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.CrateTech;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CrateTech.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CTNetworkHandler {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                OpenCollectorUpgradePacket.TYPE,
                OpenCollectorUpgradePacket.STREAM_CODEC,
                OpenCollectorUpgradePacket::handle
        );

        registrar.playToServer(
                CollectorAdjustmentPacket.TYPE,
                CollectorAdjustmentPacket.STREAM_CODEC,
                CollectorAdjustmentPacket::handle
        );

        registrar.playToServer(
                CollectorUniformAdjustmentPacket.TYPE,
                CollectorUniformAdjustmentPacket.STREAM_CODEC,
                CollectorUniformAdjustmentPacket::handle
        );

        registrar.playToServer(
                CollectorResetPacket.TYPE,
                CollectorResetPacket.STREAM_CODEC,
                CollectorResetPacket::handle
        );

        registrar.playToServer(
                CollectorWireframePacket.TYPE,
                CollectorWireframePacket.STREAM_CODEC,
                CollectorWireframePacket::handle
        );

        registrar.playToClient(
                SyncCollectorSettingsPacket.TYPE,
                SyncCollectorSettingsPacket.STREAM_CODEC,
                SyncCollectorSettingsPacket::handle
        );

        registrar.playToServer(
                OpenHopperUpgradePacket.TYPE,
                OpenHopperUpgradePacket.STREAM_CODEC,
                OpenHopperUpgradePacket::handle
        );

        registrar.playToServer(
                HopperSideConfigPacket.TYPE,
                HopperSideConfigPacket.STREAM_CODEC,
                HopperSideConfigPacket::handle
        );

        registrar.playToServer(
                HopperModeTogglePacket.TYPE,
                HopperModeTogglePacket.STREAM_CODEC,
                HopperModeTogglePacket::handle
        );

        registrar.playToServer(
                HopperResetPacket.TYPE,
                HopperResetPacket.STREAM_CODEC,
                HopperResetPacket::handle
        );

        registrar.playToClient(
                SyncHopperSettingsPacket.TYPE,
                SyncHopperSettingsPacket.STREAM_CODEC,
                SyncHopperSettingsPacket::handle
        );

        registrar.playToServer(
                OpenCompactingUpgradePacket.TYPE,
                OpenCompactingUpgradePacket.STREAM_CODEC,
                OpenCompactingUpgradePacket::handle
        );

        registrar.playToServer(
                CompactingFilterUpdatePacket.TYPE,
                CompactingFilterUpdatePacket.STREAM_CODEC,
                CompactingFilterUpdatePacket::handle
        );

        registrar.playToServer(
                CompactingModeTogglePacket.TYPE,
                CompactingModeTogglePacket.STREAM_CODEC,
                CompactingModeTogglePacket::handle
        );

        registrar.playToServer(
                CompactingResetPacket.TYPE,
                CompactingResetPacket.STREAM_CODEC,
                CompactingResetPacket::handle
        );

        registrar.playToClient(
                SyncCompactingSettingsPacket.TYPE,
                SyncCompactingSettingsPacket.STREAM_CODEC,
                SyncCompactingSettingsPacket::handle
        );

        registrar.playToServer(
                ItemFilterUpdatePacket.TYPE,
                ItemFilterUpdatePacket.STREAM_CODEC,
                ItemFilterUpdatePacket::handle
        );

        registrar.playToServer(
                ItemFilterModeTogglePacket.TYPE,
                ItemFilterModeTogglePacket.STREAM_CODEC,
                ItemFilterModeTogglePacket::handle
        );

        registrar.playToClient(
                SyncItemFilterSettingsPacket.TYPE,
                SyncItemFilterSettingsPacket.STREAM_CODEC,
                SyncItemFilterSettingsPacket::handle
        );
    }

    public static void sendToServer(OpenCollectorUpgradePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(CollectorAdjustmentPacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(CollectorUniformAdjustmentPacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(CollectorResetPacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(CollectorWireframePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToClient(SyncCollectorSettingsPacket packet) {
        PacketDistributor.sendToAllPlayers(packet);
    }

    public static void sendToPlayer(ServerPlayer player, SyncCollectorSettingsPacket packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToServer(OpenHopperUpgradePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(HopperSideConfigPacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(HopperModeTogglePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(HopperResetPacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToPlayer(ServerPlayer player, SyncHopperSettingsPacket packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToServer(OpenCompactingUpgradePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(CompactingFilterUpdatePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(CompactingModeTogglePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(CompactingResetPacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToPlayer(ServerPlayer player, SyncCompactingSettingsPacket packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToServer(ItemFilterUpdatePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(ItemFilterModeTogglePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToPlayer(ServerPlayer player, SyncItemFilterSettingsPacket packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}