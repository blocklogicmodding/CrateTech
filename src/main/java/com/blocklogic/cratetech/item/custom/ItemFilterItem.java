package com.blocklogic.cratetech.item.custom;

import com.blocklogic.cratetech.component.CTDataComponents;
import com.blocklogic.cratetech.component.ItemFilterSettings;
import com.blocklogic.cratetech.screen.custom.ItemFilterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemFilterItem extends Item {

    public ItemFilterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> new ItemFilterMenu(containerId, playerInventory, itemStack),
                    Component.translatable("gui.cratetech.item_filter.title")
            ), buf -> ItemStack.STREAM_CODEC.encode(buf, itemStack));
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(Component.translatable("tooltip.cratetech.item_filter.description")
                .withStyle(ChatFormatting.GOLD));

        ItemFilterSettings settings = stack.get(CTDataComponents.ITEM_FILTER_SETTINGS.get());
        if (settings != null && !settings.filterItems().isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.cratetech.item_filter.configured", settings.filterItems().size())
                    .withStyle(ChatFormatting.GREEN));

            tooltipComponents.add(Component.translatable(
                            settings.whitelistMode() ? "tooltip.cratetech.item_filter.whitelist" : "tooltip.cratetech.item_filter.blacklist")
                    .withStyle(ChatFormatting.GRAY));

            if (settings.matchTags()) {
                tooltipComponents.add(Component.translatable("tooltip.cratetech.item_filter.match_tags")
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }
}