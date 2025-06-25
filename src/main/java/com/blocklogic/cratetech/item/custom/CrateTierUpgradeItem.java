package com.blocklogic.cratetech.item.custom;

import com.blocklogic.cratetech.block.CTBlocks;
import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.component.CrateContents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CrateTierUpgradeItem extends Item {
    private final String fromTier;
    private final String toTier;
    private final int fromSlots;
    private final int toSlots;

    public CrateTierUpgradeItem(Properties properties, String fromTier, String toTier, int fromSlots, int toSlots) {
        super(properties);
        this.fromTier = fromTier;
        this.toTier = toTier;
        this.fromSlots = fromSlots;
        this.toSlots = toSlots;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack upgradeItem = context.getItemInHand();

        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof BaseCrateBlockEntity crateEntity)) {
            return InteractionResult.PASS;
        }

        BlockState currentState = level.getBlockState(pos);
        Block targetBlock = getTargetBlock(currentState.getBlock());

        if (targetBlock == null) {
            player.displayClientMessage(Component.translatable("message.cratetech.upgrade.invalid_crate")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        int oldStorageSlots = getStorageSlotCount(fromSlots);
        NonNullList<ItemStack> storageItems = NonNullList.withSize(oldStorageSlots, ItemStack.EMPTY);
        for (int i = 0; i < oldStorageSlots; i++) {
            storageItems.set(i, crateEntity.getItemHandler().getStackInSlot(i));
        }

        ItemStack filterItem = crateEntity.getItemHandler().getStackInSlot(getFilterSlotIndex(fromSlots));
        List<ItemStack> upgradeItems = new ArrayList<>();
        int oldUpgradeStart = getUpgradeSlotStart(fromSlots);
        for (int i = 0; i < 4; i++) {
            upgradeItems.add(crateEntity.getItemHandler().getStackInSlot(oldUpgradeStart + i));
        }

        ItemStack pushFilter = crateEntity.getItemHandler().getStackInSlot(getPushFilterIndex(fromSlots));
        ItemStack pullFilter = crateEntity.getItemHandler().getStackInSlot(getPullFilterIndex(fromSlots));

        CrateContents contents = CrateContents.fromCrate(
                storageItems,
                crateEntity.getCollectorSettings(),
                crateEntity.getHopperSettings(),
                crateEntity.getCompactingSettings(),
                oldStorageSlots
        );

        BlockState newState = targetBlock.defaultBlockState();
        level.setBlock(pos, newState, 3);

        BlockEntity newBlockEntity = level.getBlockEntity(pos);
        if (newBlockEntity instanceof BaseCrateBlockEntity newCrateEntity) {
            int newStorageSlots = getStorageSlotCount(toSlots);
            for (int i = 0; i < Math.min(contents.items().size(), newStorageSlots); i++) {
                newCrateEntity.getItemHandler().setStackInSlot(i, contents.items().get(i));
            }

            if (!filterItem.isEmpty()) {
                newCrateEntity.getItemHandler().setStackInSlot(getFilterSlotIndex(toSlots), filterItem);
            }

            int newUpgradeStart = getUpgradeSlotStart(toSlots);
            for (int i = 0; i < Math.min(upgradeItems.size(), 4); i++) {
                if (!upgradeItems.get(i).isEmpty()) {
                    newCrateEntity.getItemHandler().setStackInSlot(newUpgradeStart + i, upgradeItems.get(i));
                }
            }

            if (!pushFilter.isEmpty()) {
                newCrateEntity.getItemHandler().setStackInSlot(getPushFilterIndex(toSlots), pushFilter);
            }
            if (!pullFilter.isEmpty()) {
                newCrateEntity.getItemHandler().setStackInSlot(getPullFilterIndex(toSlots), pullFilter);
            }

            newCrateEntity.setCollectorSettings(contents.collectorSettings());
            newCrateEntity.setHopperSettings(contents.hopperSettings());
            newCrateEntity.setCompactingSettings(contents.compactingSettings());

            newCrateEntity.setChanged();
        }

        if (!player.getAbilities().instabuild) {
            upgradeItem.shrink(1);
        }

        player.displayClientMessage(Component.translatable("message.cratetech.upgrade.success", fromTier, toTier)
                .withStyle(ChatFormatting.GREEN), true);

        return InteractionResult.CONSUME;
    }

    private int getStorageSlotCount(int totalSlots) {
        if (totalSlots == 9) return 9;
        if (totalSlots == 27) return 27;
        if (totalSlots == 54) return 54;
        if (totalSlots == 104) return 104;
        return 0;
    }

    private int getFilterSlotIndex(int totalSlots) {
        if (totalSlots == 9) return 9;
        if (totalSlots == 27) return 27;
        if (totalSlots == 54) return 54;
        if (totalSlots == 104) return 104;
        return 0;
    }

    private int getUpgradeSlotStart(int totalSlots) {
        if (totalSlots == 9) return 10;
        if (totalSlots == 27) return 28;
        if (totalSlots == 54) return 55;
        if (totalSlots == 104) return 105;
        return 0;
    }

    private int getPushFilterIndex(int totalSlots) {
        if (totalSlots == 9) return 14;
        if (totalSlots == 27) return 32;
        if (totalSlots == 54) return 59;
        if (totalSlots == 104) return 109;
        return 0;
    }

    private int getPullFilterIndex(int totalSlots) {
        if (totalSlots == 9) return 15;
        if (totalSlots == 27) return 33;
        if (totalSlots == 54) return 60;
        if (totalSlots == 104) return 110;
        return 0;
    }

    private Block getTargetBlock(Block currentBlock) {
        if (fromTier.equals("Small") && toTier.equals("Medium") && currentBlock == CTBlocks.SMALL_CRATE.get()) {
            return CTBlocks.MEDIUM_CRATE.get();
        } else if (fromTier.equals("Medium") && toTier.equals("Large") && currentBlock == CTBlocks.MEDIUM_CRATE.get()) {
            return CTBlocks.LARGE_CRATE.get();
        } else if (fromTier.equals("Large") && toTier.equals("Huge") && currentBlock == CTBlocks.LARGE_CRATE.get()) {
            return CTBlocks.HUGE_CRATE.get();
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(Component.translatable("tooltip.cratetech.tier_upgrade.description", fromTier, toTier)
                .withStyle(ChatFormatting.GOLD));

        tooltipComponents.add(Component.translatable("tooltip.cratetech.tier_upgrade.capacity", fromSlots, toSlots)
                .withStyle(ChatFormatting.GREEN));

        tooltipComponents.add(Component.translatable("tooltip.cratetech.tier_upgrade.preserves")
                .withStyle(ChatFormatting.GREEN));

        tooltipComponents.add(Component.translatable("tooltip.cratetech.tier_upgrade.usage")
                .withStyle(ChatFormatting.AQUA));
    }
}