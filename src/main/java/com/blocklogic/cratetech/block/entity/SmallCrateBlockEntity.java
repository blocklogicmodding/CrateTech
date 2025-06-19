package com.blocklogic.cratetech.block.entity;

import com.blocklogic.cratetech.screen.custom.SmallCrateMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;

public class SmallCrateBlockEntity extends BaseCrateBlockEntity {
    public static final int INVENTORY_SIZE = 14;

    public SmallCrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(CTBlockEntities.SMALL_CRATE_BE.get(), pos, blockState, INVENTORY_SIZE);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.cratetech.small_crate");
    }

    @Override
    protected MenuType<?> getMenuType() {
        return null; // Using custom menu
    }

    @Override
    protected int getMenuRows() {
        return 3;
    }

    @Override
    public AbstractContainerMenu createCustomMenu(int containerId, Inventory playerInventory, Player player) {
        return new SmallCrateMenu(containerId, playerInventory, this.itemHandler, this.getBlockPos());
    }
}