package com.blocklogic.cratetech.screen.custom;

import com.blocklogic.cratetech.item.CTItems;
import com.blocklogic.cratetech.screen.CTMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SmallCrateMenu extends AbstractContainerMenu {
    private final IItemHandler itemHandler;
    private final int storageSlots = 9;
    private static final int FILTER_SLOT = 9;
    private static final int UPGRADE_SLOTS_START = 10;
    private static final int UPGRADE_SLOTS_COUNT = 4;

    private final BlockPos cratePos;

    public SmallCrateMenu(int containerId, Inventory playerInventory, IItemHandler itemHandler) {
        this(containerId, playerInventory, itemHandler, BlockPos.ZERO);
    }

    public BlockPos getCratePos() {
        return cratePos;
    }

    public SmallCrateMenu(int containerId, Inventory playerInventory, IItemHandler itemHandler, BlockPos cratePos) {
        super(CTMenuTypes.SMALL_CRATE_MENU.get(), containerId);
        this.itemHandler = itemHandler;
        this.cratePos = cratePos;

        int slotIndex = 0;
        for (int row = 0; row < 1; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new SlotItemHandler(itemHandler, slotIndex++,
                        8 + col * 18, 16 + row * 18));
            }
        }

        this.addSlot(new SlotItemHandler(itemHandler, FILTER_SLOT, -26, 8) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == CTItems.ITEM_FILTER.get();
            }
        });

        for (int i = 0; i < UPGRADE_SLOTS_COUNT; i++) {
            final int slotIndex_upgrade = UPGRADE_SLOTS_START + i;
            this.addSlot(new SlotItemHandler(itemHandler, slotIndex_upgrade,
                    -26, 45 + i * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    if (!(stack.getItem() == CTItems.COLLECTOR_UPGRADE.get() ||
                            stack.getItem() == CTItems.HOPPER_UPGRADE.get() ||
                            stack.getItem() == CTItems.COMPACTING_UPGRADE.get() ||
                            stack.getItem() == CTItems.SHULKER_UPGRADE.get())) {
                        return false;
                    }

                    for (int j = 0; j < UPGRADE_SLOTS_COUNT; j++) {
                        int checkSlot = UPGRADE_SLOTS_START + j;
                        if (checkSlot != slotIndex_upgrade) {
                            ItemStack existingStack = itemHandler.getStackInSlot(checkSlot);
                            if (!existingStack.isEmpty() && existingStack.getItem() == stack.getItem()) {
                                return false;
                            }
                        }
                    }

                    return true;
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 46 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    8 + col * 18, 104));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < storageSlots) {
                if (!this.moveItemStackTo(slotStack, storageSlots + 1 + UPGRADE_SLOTS_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (index == FILTER_SLOT) {
                if (!this.moveItemStackTo(slotStack, storageSlots + 1 + UPGRADE_SLOTS_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= UPGRADE_SLOTS_START && index < UPGRADE_SLOTS_START + UPGRADE_SLOTS_COUNT) {
                if (!this.moveItemStackTo(slotStack, storageSlots + 1 + UPGRADE_SLOTS_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (slotStack.getItem() == CTItems.ITEM_FILTER.get()) {
                    if (!this.moveItemStackTo(slotStack, FILTER_SLOT, FILTER_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (slotStack.getItem() == CTItems.COLLECTOR_UPGRADE.get() ||
                        slotStack.getItem() == CTItems.HOPPER_UPGRADE.get() ||
                        slotStack.getItem() == CTItems.COMPACTING_UPGRADE.get() ||
                        slotStack.getItem() == CTItems.SHULKER_UPGRADE.get()) {
                    if (!this.moveItemStackTo(slotStack, UPGRADE_SLOTS_START, UPGRADE_SLOTS_START + UPGRADE_SLOTS_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else {
                    if (!this.moveItemStackTo(slotStack, 0, storageSlots, false)) {
                        return ItemStack.EMPTY;
                    }
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
        return true;
    }
}