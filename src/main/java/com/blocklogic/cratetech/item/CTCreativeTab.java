package com.blocklogic.cratetech.item;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.block.CTBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CTCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrateTech.MODID);

    public static final Supplier<CreativeModeTab> CRATETECH = CREATIVE_MODE_TAB.register("cratetech",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(CTBlocks.SMALL_CRATE.get()))
                    .title(Component.translatable("cratetech.cratetech.cratetech_creative_tab"))
                    .displayItems((ItemDisplayParameters, output) -> {
                        output.accept(CTBlocks.SMALL_CRATE);
                        output.accept(CTBlocks.MEDIUM_CRATE);
                        output.accept(CTBlocks.LARGE_CRATE);
                        output.accept(CTBlocks.HUGE_CRATE);

                        output.accept(CTItems.UPGRADE_BASE);
                        output.accept(CTItems.COLLECTOR_UPGRADE);
                        output.accept(CTItems.HOPPER_UPGRADE);
                        output.accept(CTItems.COMPACTING_UPGRADE);
                        output.accept(CTItems.SHULKER_UPGRADE);
                        output.accept(CTItems.ITEM_FILTER);
                        output.accept(CTItems.SMALL_TO_MEDIUM_CRATE_UPGRADE);
                        output.accept(CTItems.MEDIUM_TO_LARGE_CRATE_UPGRADE);
                        output.accept(CTItems.LARGE_TO_HUGE_CRATE_UPGRADE);
                    }).build());

    public static void register (IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
