package com.blocklogic.cratetech.screen.custom;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class CompactingGhostItemHandler implements IItemHandlerModifiable {
    private final ItemStack[] stacks;
    private final CompactingUpgradeMenu menu;

    public CompactingGhostItemHandler(int size, CompactingUpgradeMenu menu) {
        this.stacks = new ItemStack[size];
        this.menu = menu;
        for (int i = 0; i < size; i++) {
            this.stacks[i] = ItemStack.EMPTY;
        }
    }

    public void notifyFilterUpdate() {
        if (menu != null) {
            menu.updateFilterItems();
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot >= 0 && slot < stacks.length) {
            stacks[slot] = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            if (stack.getCount() > 1) {
                stacks[slot].setCount(1);
            }
        }
    }

    @Override
    public int getSlots() {
        return stacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < stacks.length ? stacks[slot] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot < 0 || slot >= stacks.length || stack.isEmpty()) {
            return stack;
        }

        if (!simulate) {
            ItemStack ghostStack = stack.copy();
            ghostStack.setCount(1);
            stacks[slot] = ghostStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < 0 || slot >= stacks.length) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = stacks[slot];
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!simulate) {
            stacks[slot] = ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < stacks.length) {
            stacks[slot] = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        }
    }
}