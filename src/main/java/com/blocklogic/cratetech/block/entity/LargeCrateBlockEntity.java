package com.blocklogic.cratetech.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;

public class LargeCrateBlockEntity extends BaseCrateBlockEntity {
    public static final int INVENTORY_SIZE = 54; // 6x9 = 54 slots

    public LargeCrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(CTBlockEntities.LARGE_CRATE_BE.get(), pos, blockState, INVENTORY_SIZE);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.cratetech.large_crate");
    }

    @Override
    protected MenuType<?> getMenuType() {
        return MenuType.GENERIC_9x6;
    }

    @Override
    protected int getMenuRows() {
        return 6;
    }
}