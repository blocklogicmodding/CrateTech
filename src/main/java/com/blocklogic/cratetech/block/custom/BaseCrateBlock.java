package com.blocklogic.cratetech.block.custom;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.network.CTNetworkHandler;
import com.blocklogic.cratetech.network.SyncCollectorSettingsPacket;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class BaseCrateBlock extends BaseEntityBlock {

    public BaseCrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected abstract MapCodec<? extends BaseCrateBlock> codec();

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof BaseCrateBlockEntity crateEntity) {
                if (player instanceof ServerPlayer serverPlayer) {
                    CTNetworkHandler.sendToPlayer(serverPlayer, new SyncCollectorSettingsPacket(pos, crateEntity.getCollectorSettings()));
                }

                player.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, p) -> crateEntity.createCustomMenu(containerId, playerInventory, p),
                        crateEntity.getDisplayName()
                ), buf -> buf.writeBlockPos(pos));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                crateEntity.dropContents(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(blockEntityType, getBlockEntityType(), BaseCrateBlockEntity::serverTick);
    }

    protected abstract BlockEntityType<? extends BaseCrateBlockEntity> getBlockEntityType();
}