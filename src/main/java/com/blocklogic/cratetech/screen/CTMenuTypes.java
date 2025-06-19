package com.blocklogic.cratetech.screen;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.block.entity.HugeCrateBlockEntity;
import com.blocklogic.cratetech.screen.custom.HugeCrateMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CTMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, CrateTech.MODID);

    public static final Supplier<MenuType<HugeCrateMenu>> HUGE_CRATE_MENU =
            MENUS.register("huge_crate_menu", () ->
                    IMenuTypeExtension.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        Level level = inv.player.level();
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof HugeCrateBlockEntity hugeCrate) {
                            return new HugeCrateMenu(windowId, inv, hugeCrate.getItemHandler());
                        }
                        throw new IllegalStateException("Block entity is not a HugeCrateBlockEntity!");
                    }));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}