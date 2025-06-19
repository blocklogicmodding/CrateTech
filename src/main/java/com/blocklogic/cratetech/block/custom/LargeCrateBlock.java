package com.blocklogic.cratetech.block.custom;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.block.entity.CTBlockEntities;
import com.blocklogic.cratetech.block.entity.LargeCrateBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LargeCrateBlock extends BaseCrateBlock {
    public static final MapCodec<LargeCrateBlock> CODEC = simpleCodec(LargeCrateBlock::new);

    public LargeCrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<LargeCrateBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LargeCrateBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<? extends BaseCrateBlockEntity> getBlockEntityType() {
        return CTBlockEntities.LARGE_CRATE_BE.get();
    }
}