package com.blocklogic.cratetech.block.entity;

import com.blocklogic.cratetech.screen.custom.HugeCrateMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HugeCrateBlockEntity extends BaseCrateBlockEntity {
    public static final int INVENTORY_SIZE = 109;

    public HugeCrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(CTBlockEntities.HUGE_CRATE_BE.get(), pos, blockState, INVENTORY_SIZE);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.cratetech.huge_crate");
    }

    @Override
    protected MenuType<?> getMenuType() {
        return null;
    }

    @Override
    protected int getMenuRows() {
        return 8;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HugeCrateMenu(containerId, playerInventory, this.itemHandler);
    }
}