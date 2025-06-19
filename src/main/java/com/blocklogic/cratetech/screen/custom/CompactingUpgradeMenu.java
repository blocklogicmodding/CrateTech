package com.blocklogic.cratetech.screen.custom;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.component.CompactingSettings;
import com.blocklogic.cratetech.network.CTNetworkHandler;
import com.blocklogic.cratetech.network.CompactingFilterUpdatePacket;
import com.blocklogic.cratetech.network.CompactingModeTogglePacket;
import com.blocklogic.cratetech.network.CompactingResetPacket;
import com.blocklogic.cratetech.screen.CTMenuTypes;
import net.minecraft.core.BlockPos;
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

public class CompactingUpgradeMenu extends AbstractContainerMenu {
    private final BaseCrateBlockEntity crateEntity;
    private final Level level;
    private final BlockPos pos;

    private final CompactingGhostItemHandler ghostItemHandler;

    public CompactingUpgradeMenu(int containerId, Inventory playerInventory, BaseCrateBlockEntity crateEntity) {
        super(CTMenuTypes.COMPACTING_UPGRADE_MENU.get(), containerId);
        this.crateEntity = crateEntity;
        this.level = playerInventory.player.level();
        this.pos = crateEntity.getBlockPos();
        this.ghostItemHandler = new CompactingGhostItemHandler(18, this);

        CompactingSettings settings = crateEntity.getCompactingSettings();
        List<Item> filterItems = settings.filterItems();
        for (int i = 0; i < Math.min(18, filterItems.size()); i++) {
            ghostItemHandler.setItem(i, new ItemStack(filterItems.get(i)));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 6; col++) {
                int slotIndex = row * 6 + col;
                this.addSlot(new CompactingGhostSlot(ghostItemHandler, slotIndex,
                        35 + col * 18, 16 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 82 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    8 + col * 18, 141));
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < 18) {
            handleGhostSlotClick(slotId, button, clickType, player);
            return;
        }

        super.clicked(slotId, button, clickType, player);
    }

    private void handleGhostSlotClick(int slotId, int button, ClickType clickType, Player player) {
        ItemStack carriedStack = getCarried();

        if (clickType == ClickType.PICKUP) {
            if (button == 0) {
                if (!carriedStack.isEmpty()) {
                    ItemStack ghostStack = carriedStack.copy();
                    ghostStack.setCount(1);
                    ghostItemHandler.setStackInSlot(slotId, ghostStack);
                } else {
                    ghostItemHandler.setStackInSlot(slotId, ItemStack.EMPTY);
                }
            } else if (button == 1) {
                ghostItemHandler.setStackInSlot(slotId, ItemStack.EMPTY);
            }
        } else if (clickType == ClickType.QUICK_MOVE) {
            return;
        }

        updateFilterItems();
        broadcastChanges();
    }

    public BaseCrateBlockEntity getCrateEntity() {
        return crateEntity;
    }

    public void toggleWhitelistMode() {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new CompactingModeTogglePacket(pos, true));
        } else {
            crateEntity.toggleCompactingWhitelistMode();
        }
    }

    public void toggleRecipeSize() {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new CompactingModeTogglePacket(pos, false));
        } else {
            crateEntity.toggleCompacting3x3Mode();
        }
    }

    public void resetCompactingSettings() {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new CompactingResetPacket(pos));
        } else {
            crateEntity.resetCompactingSettings();
        }
    }

    public void updateFilterItems() {
        List<Item> filterItems = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            ItemStack stack = ghostItemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                filterItems.add(stack.getItem());
            }
        }

        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new CompactingFilterUpdatePacket(pos, filterItems));
        } else {
            crateEntity.updateCompactingFilter(filterItems);
        }
    }

    public boolean isWhitelistMode() {
        return crateEntity.getCompactingSettings().whitelistMode();
    }

    public boolean isUse3x3Recipes() {
        return crateEntity.getCompactingSettings().use3x3Recipes();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 18) {
            return ItemStack.EMPTY;
        }

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index >= 18 && index < 45) {
                if (!this.moveItemStackTo(slotStack, 45, 54, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 45 && index < 54) {
                if (!this.moveItemStackTo(slotStack, 18, 45, false)) {
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
        return crateEntity != null && !crateEntity.isRemoved() &&
                player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
}