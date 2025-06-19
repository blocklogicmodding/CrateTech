package com.blocklogic.cratetech.block.custom;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.block.entity.CTBlockEntities;
import com.blocklogic.cratetech.block.entity.HugeCrateBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HugeCrateBlock extends BaseCrateBlock {
    public static final MapCodec<HugeCrateBlock> CODEC = simpleCodec(HugeCrateBlock::new);

    public HugeCrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<HugeCrateBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HugeCrateBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<? extends BaseCrateBlockEntity> getBlockEntityType() {
        return CTBlockEntities.HUGE_CRATE_BE.get();
    }
}