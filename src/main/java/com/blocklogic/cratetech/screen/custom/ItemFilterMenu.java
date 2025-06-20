package com.blocklogic.cratetech.screen.custom;

import com.blocklogic.cratetech.component.ItemFilterSettings;
import com.blocklogic.cratetech.network.CTNetworkHandler;
import com.blocklogic.cratetech.network.ItemFilterModeTogglePacket;
import com.blocklogic.cratetech.screen.CTMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ItemFilterMenu extends AbstractContainerMenu {
    private final Level level;
    private final ItemStack filterItemStack;
    private final ItemFilterGhostItemHandler ghostItemHandler;

    public ItemFilterMenu(int containerId, Inventory playerInventory, ItemStack filterItemStack) {
        super(CTMenuTypes.ITEM_FILTER_MENU.get(), containerId);
        this.level = playerInventory.player.level();
        this.filterItemStack = filterItemStack;
        this.ghostItemHandler = new ItemFilterGhostItemHandler(27, this);

        // Load existing filter settings
        ItemFilterSettings settings = getFilterSettings();
        List<Item> filterItems = settings.filterItems();
        for (int i = 0; i < Math.min(27, filterItems.size()); i++) {
            ghostItemHandler.setItem(i, new ItemStack(filterItems.get(i)));
        }

        // Add filter ghost slots (9x3 grid)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = row * 9 + col;
                this.addSlot(new ItemFilterGhostSlot(ghostItemHandler, slotIndex,
                        8 + col * 18, 29 + row * 18));
            }
        }

        // Add player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 95 + row * 18));
            }
        }

        // Add player hotbar slots
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    8 + col * 18, 155));
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < 27) {
            handleGhostSlotClick(slotId, button, clickType, player);
            return;
        }

        super.clicked(slotId, button, clickType, player);
    }

    private void handleGhostSlotClick(int slotId, int button, ClickType clickType, Player player) {
        ItemStack carriedStack = getCarried();

        if (clickType == ClickType.PICKUP) {
            if (button == 0) { // Left click
                if (!carriedStack.isEmpty()) {
                    ItemStack ghostStack = carriedStack.copy();
                    ghostStack.setCount(1);
                    ghostItemHandler.setStackInSlot(slotId, ghostStack);
                } else {
                    ghostItemHandler.setStackInSlot(slotId, ItemStack.EMPTY);
                }
            } else if (button == 1) { // Right click
                ghostItemHandler.setStackInSlot(slotId, ItemStack.EMPTY);
            }
        } else if (clickType == ClickType.QUICK_MOVE) {
            return;
        }

        updateFilterItems();
        broadcastChanges();
    }

    public void updateFilterItems() {
        List<Item> filterItems = new ArrayList<>();
        for (int i = 0; i < 27; i++) {
            ItemStack stack = ghostItemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                filterItems.add(stack.getItem());
            }
        }

        ItemFilterSettings currentSettings = getFilterSettings();
        ItemFilterSettings newSettings = currentSettings.withFilterItems(filterItems);
        setFilterSettings(newSettings);
    }

    public void toggleWhitelistMode() {
        // Update locally for immediate visual feedback
        ItemFilterSettings currentSettings = getFilterSettings();
        ItemFilterSettings newSettings = currentSettings.withWhitelistMode(!currentSettings.whitelistMode());
        setFilterSettings(newSettings);

        // Send to server so it persists
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new ItemFilterModeTogglePacket(true));
        }
    }

    public void toggleMatchTags() {
        ItemFilterSettings currentSettings = getFilterSettings();
        ItemFilterSettings newSettings = currentSettings.withMatchTags(!currentSettings.matchTags());
        setFilterSettings(newSettings);

        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new ItemFilterModeTogglePacket(false));
        }
    }

    public boolean isWhitelistMode() {
        return getFilterSettings().whitelistMode();
    }

    public boolean isMatchTags() {
        return getFilterSettings().matchTags();
    }

    private ItemFilterSettings getFilterSettings() {
        ItemFilterSettings settings = filterItemStack.get(com.blocklogic.cratetech.component.CTDataComponents.ITEM_FILTER_SETTINGS.get());
        return settings != null ? settings : ItemFilterSettings.DEFAULT;
    }

    private void setFilterSettings(ItemFilterSettings settings) {
        filterItemStack.set(com.blocklogic.cratetech.component.CTDataComponents.ITEM_FILTER_SETTINGS.get(), settings);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 27) {
            return ItemStack.EMPTY; // Ghost slots don't transfer
        }

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index >= 27 && index < 54) { // Player inventory
                if (!this.moveItemStackTo(slotStack, 54, 63, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 54 && index < 63) { // Player hotbar
                if (!this.moveItemStackTo(slotStack, 27, 54, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().contains(filterItemStack);
    }
}