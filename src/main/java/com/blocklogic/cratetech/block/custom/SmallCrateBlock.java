package com.blocklogic.cratetech.block.custom;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.block.entity.CTBlockEntities;
import com.blocklogic.cratetech.block.entity.SmallCrateBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SmallCrateBlock extends BaseCrateBlock {
    public static final MapCodec<SmallCrateBlock> CODEC = simpleCodec(SmallCrateBlock::new);

    public SmallCrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<SmallCrateBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmallCrateBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<? extends BaseCrateBlockEntity> getBlockEntityType() {
        return CTBlockEntities.SMALL_CRATE_BE.get();
    }
}