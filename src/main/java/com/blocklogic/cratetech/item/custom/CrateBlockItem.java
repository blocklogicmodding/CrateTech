package com.blocklogic.cratetech.item.custom;

import com.blocklogic.cratetech.component.CTDataComponents;
import com.blocklogic.cratetech.component.CrateContents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class CrateBlockItem extends BlockItem {

    public CrateBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        CrateContents contents = stack.get(CTDataComponents.CRATE_CONTENTS.get());
        if (contents != null && !contents.isEmpty()) {
            int itemCount = 0;
            int stackCount = 0;

            for (ItemStack item : contents.items()) {
                if (!item.isEmpty()) {
                    itemCount++;
                    stackCount += item.getCount();
                }
            }

            if (itemCount > 0) {
                tooltipComponents.add(Component.translatable("tooltip.cratetech.crate.contains_items", itemCount, stackCount)
                        .withStyle(ChatFormatting.GRAY));
            }

            if (contents.hasUpgrades()) {
                tooltipComponents.add(Component.translatable("tooltip.cratetech.crate.configured_upgrades")
                        .withStyle(ChatFormatting.GREEN));
            }

            tooltipComponents.add(Component.translatable("tooltip.cratetech.crate.shulker_enabled")
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        CrateContents contents = stack.get(CTDataComponents.CRATE_CONTENTS.get());
        return contents != null && !contents.isEmpty();
    }
}