package com.blocklogic.cratetech;

import com.blocklogic.cratetech.block.CTBlocks;
import com.blocklogic.cratetech.block.entity.CTBlockEntities;
import com.blocklogic.cratetech.item.CTCreativeTab;
import com.blocklogic.cratetech.item.CTItems;
import com.blocklogic.cratetech.screen.CTMenuTypes;
import com.blocklogic.cratetech.screen.custom.HugeCrateScreen;
import com.blocklogic.cratetech.screen.custom.LargeCrateScreen;
import com.blocklogic.cratetech.screen.custom.MediumCrateScreen;
import com.blocklogic.cratetech.screen.custom.SmallCrateScreen;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(CrateTech.MODID)
public class CrateTech {
    public static final String MODID = "cratetech";

    private static final Logger LOGGER = LogUtils.getLogger();

    public CrateTech(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        CTItems.register(modEventBus);
        CTBlocks.register(modEventBus);
        CTBlockEntities.register(modEventBus);
        CTMenuTypes.register(modEventBus);
        CTCreativeTab.register(modEventBus);

        modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(CTMenuTypes.SMALL_CRATE_MENU.get(), SmallCrateScreen::new);
            event.register(CTMenuTypes.MEDIUM_CRATE_MENU.get(), MediumCrateScreen::new);
            event.register(CTMenuTypes.LARGE_CRATE_MENU.get(), LargeCrateScreen::new);
            event.register(CTMenuTypes.HUGE_CRATE_MENU.get(), HugeCrateScreen::new);
        }
    }
}