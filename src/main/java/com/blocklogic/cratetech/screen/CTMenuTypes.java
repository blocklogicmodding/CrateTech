package com.blocklogic.cratetech.screen;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.block.entity.HugeCrateBlockEntity;
import com.blocklogic.cratetech.block.entity.LargeCrateBlockEntity;
import com.blocklogic.cratetech.block.entity.MediumCrateBlockEntity;
import com.blocklogic.cratetech.block.entity.SmallCrateBlockEntity;
import com.blocklogic.cratetech.screen.custom.HugeCrateMenu;
import com.blocklogic.cratetech.screen.custom.LargeCrateMenu;
import com.blocklogic.cratetech.screen.custom.MediumCrateMenu;
import com.blocklogic.cratetech.screen.custom.SmallCrateMenu;
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

    public static final Supplier<MenuType<SmallCrateMenu>> SMALL_CRATE_MENU =
            MENUS.register("small_crate_menu", () ->
                    IMenuTypeExtension.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        Level level = inv.player.level();
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof SmallCrateBlockEntity smallCrate) {
                            return new SmallCrateMenu(windowId, inv, smallCrate.getItemHandler());
                        }
                        throw new IllegalStateException("Block entity is not a SmallCrateBlockEntity!");
                    }));

    public static final Supplier<MenuType<MediumCrateMenu>> MEDIUM_CRATE_MENU =
            MENUS.register("medium_crate_menu", () ->
                    IMenuTypeExtension.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        Level level = inv.player.level();
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof MediumCrateBlockEntity mediumCrate) {
                            return new MediumCrateMenu(windowId, inv, mediumCrate.getItemHandler());
                        }
                        throw new IllegalStateException("Block entity is not a MediumCrateBlockEntity!");
                    }));

    public static final Supplier<MenuType<LargeCrateMenu>> LARGE_CRATE_MENU =
            MENUS.register("large_crate_menu", () ->
                    IMenuTypeExtension.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        Level level = inv.player.level();
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof LargeCrateBlockEntity largeCrate) {
                            return new LargeCrateMenu(windowId, inv, largeCrate.getItemHandler());
                        }
                        throw new IllegalStateException("Block entity is not a LargeCrateBlockEntity!");
                    }));

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