package com.blocklogic.cratetech.block.entity;

import com.blocklogic.cratetech.screen.custom.LargeCrateMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;

public class LargeCrateBlockEntity extends BaseCrateBlockEntity {
    public static final int INVENTORY_SIZE = 61;

    public LargeCrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(CTBlockEntities.LARGE_CRATE_BE.get(), pos, blockState, 59);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.cratetech.large_crate");
    }

    @Override
    protected MenuType<?> getMenuType() {
        return null; // Using custom menu
    }

    @Override
    protected int getMenuRows() {
        return 6;
    }

    @Override
    public AbstractContainerMenu createCustomMenu(int containerId, Inventory playerInventory, Player player) {
        return new LargeCrateMenu(containerId, playerInventory, this.itemHandler, this.getBlockPos());
    }
}