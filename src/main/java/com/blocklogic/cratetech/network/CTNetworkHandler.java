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
    }

    public static void sendToServer(OpenCollectorUpgradePacket packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToServer(CollectorAdjustmentPacket packet) {
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
}