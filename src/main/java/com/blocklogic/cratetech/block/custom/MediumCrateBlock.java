package com.blocklogic.cratetech.block.custom;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.block.entity.CTBlockEntities;
import com.blocklogic.cratetech.block.entity.MediumCrateBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MediumCrateBlock extends BaseCrateBlock {
    public static final MapCodec<MediumCrateBlock> CODEC = simpleCodec(MediumCrateBlock::new);

    public MediumCrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<MediumCrateBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MediumCrateBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<? extends BaseCrateBlockEntity> getBlockEntityType() {
        return CTBlockEntities.MEDIUM_CRATE_BE.get();
    }
}