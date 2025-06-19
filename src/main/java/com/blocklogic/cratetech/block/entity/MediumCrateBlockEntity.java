package com.blocklogic.cratetech.block.entity;

import com.blocklogic.cratetech.screen.custom.MediumCrateMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MediumCrateBlockEntity extends BaseCrateBlockEntity {
    public static final int INVENTORY_SIZE = 32;

    public MediumCrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(CTBlockEntities.MEDIUM_CRATE_BE.get(), pos, blockState, INVENTORY_SIZE);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.cratetech.medium_crate");
    }

    @Override
    protected MenuType<?> getMenuType() {
        return null; // Using custom menu
    }

    @Override
    protected int getMenuRows() {
        return 3;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MediumCrateMenu(containerId, playerInventory, this.itemHandler);
    }
}