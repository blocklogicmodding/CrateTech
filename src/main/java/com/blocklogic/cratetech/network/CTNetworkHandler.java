package com.blocklogic.cratetech.network;

import com.blocklogic.cratetech.CrateTech;
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
    }

    public static void sendToServer(OpenCollectorUpgradePacket packet) {
        PacketDistributor.sendToServer(packet);
    }
}