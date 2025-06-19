package com.blocklogic.cratetech.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;

public class MediumCrateBlockEntity extends BaseCrateBlockEntity {
    public static final int INVENTORY_SIZE = 27; // 3x9 = 27 slots

    public MediumCrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(CTBlockEntities.MEDIUM_CRATE_BE.get(), pos, blockState, INVENTORY_SIZE);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.cratetech.medium_crate");
    }

    @Override
    protected MenuType<?> getMenuType() {
        return MenuType.GENERIC_9x3;
    }

    @Override
    protected int getMenuRows() {
        return 3;
    }
}