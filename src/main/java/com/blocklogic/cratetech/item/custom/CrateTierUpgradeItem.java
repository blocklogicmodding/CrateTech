package com.blocklogic.cratetech.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(Component.translatable("tooltip.cratetech.tier_upgrade.description", fromTier, toTier)
                .withStyle(ChatFormatting.GOLD));

        tooltipComponents.add(Component.translatable("tooltip.cratetech.tier_upgrade.capacity", fromSlots, toSlots)
                .withStyle(ChatFormatting.GREEN));

        tooltipComponents.add(Component.translatable("tooltip.cratetech.tier_upgrade.preserves")
                .withStyle(ChatFormatting.GREEN));

        tooltipComponents.add(Component.translatable("tooltip.cratetech.tier_upgrade.usage")
                .withStyle(ChatFormatting.GREEN));
    }
}