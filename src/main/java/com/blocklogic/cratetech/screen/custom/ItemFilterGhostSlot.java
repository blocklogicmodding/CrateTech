package com.blocklogic.cratetech.screen.custom;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ItemFilterGhostSlot extends Slot {
    private final ItemFilterGhostItemHandler ghostHandler;
    private final int slotIndex;

    public ItemFilterGhostSlot(ItemFilterGhostItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(new DummyInventory(), index, xPosition, yPosition);
        this.ghostHandler = itemHandler;
        this.slotIndex = index;
    }

    @Override
    public ItemStack getItem() {
        return ghostHandler.getStackInSlot(slotIndex);
    }

    @Override
    public void set(ItemStack stack) {

    }

    @Override
    public void setByPlayer(ItemStack stack, ItemStack originalStack) {

    }

    @Override
    public ItemStack remove(int amount) {

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack safeTake(int amount, int decrement, Player player) {

        return ItemStack.EMPTY;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {

    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean hasItem() {
        return !ghostHandler.getStackInSlot(slotIndex).isEmpty();
    }

    @Override
    public void setChanged() {

    }

    private static class DummyInventory implements Container {
        @Override
        public int getContainerSize() {
            return 27;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {

        }

        @Override
        public void setChanged() {

        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {

        }
    }
}