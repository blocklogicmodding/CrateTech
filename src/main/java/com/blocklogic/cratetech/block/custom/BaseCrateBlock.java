package com.blocklogic.cratetech.block.custom;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.component.CTDataComponents;
import com.blocklogic.cratetech.component.CrateContents;
import com.blocklogic.cratetech.network.CTNetworkHandler;
import com.blocklogic.cratetech.network.SyncCollectorSettingsPacket;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        BlockEntity blockEntity = builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof BaseCrateBlockEntity crateEntity && crateEntity.hasShulkerUpgrade()) {
            ItemStack crateItem = new ItemStack(state.getBlock().asItem());

            NonNullList<ItemStack> items = NonNullList.withSize(crateEntity.getInventorySize(), ItemStack.EMPTY);
            for (int i = 0; i < crateEntity.getInventorySize(); i++) {
                items.set(i, crateEntity.getItemHandler().getStackInSlot(i));
            }

            CrateContents contents = CrateContents.fromCrate(
                    items,
                    crateEntity.getCollectorSettings(),
                    crateEntity.getHopperSettings(),
                    crateEntity.getCompactingSettings(),
                    crateEntity.getInventorySize()
            );

            crateItem.set(CTDataComponents.CRATE_CONTENTS.get(), contents);

            return List.of(crateItem);
        }
        return super.getDrops(state, builder);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                if (!crateEntity.hasShulkerUpgrade()) {
                    crateEntity.dropContents(level, pos);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        CrateContents contents = stack.get(CTDataComponents.CRATE_CONTENTS.get());
        if (contents != null && !contents.isEmpty()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseCrateBlockEntity crateEntity) {
                for (int i = 0; i < Math.min(contents.items().size(), crateEntity.getInventorySize()); i++) {
                    crateEntity.getItemHandler().setStackInSlot(i, contents.items().get(i));
                }
                crateEntity.setCollectorSettings(contents.collectorSettings());
                crateEntity.setHopperSettings(contents.hopperSettings());
                crateEntity.setCompactingSettings(contents.compactingSettings());

                crateEntity.setChanged();
            }
        }
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