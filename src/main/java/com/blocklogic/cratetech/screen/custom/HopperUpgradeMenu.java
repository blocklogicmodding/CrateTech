package com.blocklogic.cratetech.screen.custom;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.component.HopperSettings;
import com.blocklogic.cratetech.item.CTItems;
import com.blocklogic.cratetech.network.CTNetworkHandler;
import com.blocklogic.cratetech.network.HopperSideConfigPacket;
import com.blocklogic.cratetech.network.HopperModeTogglePacket;
import com.blocklogic.cratetech.network.HopperResetPacket;
import com.blocklogic.cratetech.network.SyncHopperSettingsPacket;
import com.blocklogic.cratetech.screen.CTMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;

public class HopperUpgradeMenu extends AbstractContainerMenu {
    private final BaseCrateBlockEntity crateEntity;
    private final Level level;
    private final BlockPos pos;

    // Filter slot indices
    private static final int PUSH_FILTER_SLOT = 0;
    private static final int PULL_FILTER_SLOT = 1;

    public HopperUpgradeMenu(int containerId, Inventory playerInventory, BaseCrateBlockEntity crateEntity) {
        super(CTMenuTypes.HOPPER_UPGRADE_MENU.get(), containerId);
        this.crateEntity = crateEntity;
        this.level = playerInventory.player.level();
        this.pos = crateEntity.getBlockPos();

        // Add push filter slot at x=133, y=57
        this.addSlot(new SlotItemHandler(crateEntity.getItemHandler(), crateEntity.getPushFilterSlotIndex(), 133, 57) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == CTItems.ITEM_FILTER.get();
            }
        });

        // Add pull filter slot at x=152, y=57
        this.addSlot(new SlotItemHandler(crateEntity.getItemHandler(), crateEntity.getPullFilterSlotIndex(), 152, 57) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == CTItems.ITEM_FILTER.get();
            }
        });

        // Player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 87 + row * 18));
            }
        }

        // Player hotbar slots
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    8 + col * 18, 146));
        }

        // Sync settings to client when menu opens
        if (!level.isClientSide() && playerInventory.player instanceof ServerPlayer serverPlayer) {
            CTNetworkHandler.sendToPlayer(serverPlayer, new SyncHopperSettingsPacket(pos, crateEntity.getHopperSettings()));
        }
    }

    public BaseCrateBlockEntity getCrateEntity() {
        return crateEntity;
    }

    public void toggleSideConfig(int direction) {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new HopperSideConfigPacket(pos, direction));
        } else {
            crateEntity.toggleHopperSide(direction);
        }
    }

    public void togglePushMode() {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new HopperModeTogglePacket(pos, true));
        } else {
            crateEntity.toggleHopperPushMode();
        }
    }

    public void togglePullMode() {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new HopperModeTogglePacket(pos, false));
        } else {
            crateEntity.toggleHopperPullMode();
        }
    }

    public void resetHopperSettings() {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new HopperResetPacket(pos));
        } else {
            crateEntity.resetHopperSettings();
        }
    }

    public HopperSettings.SideMode getSideMode(int direction) {
        return crateEntity.getHopperSideMode(direction);
    }

    public boolean isPushModeActive() {
        return crateEntity.isHopperPushModeActive();
    }

    public boolean isPullModeActive() {
        return crateEntity.isHopperPullModeActive();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 2) {
            // Filter slots - handle specially
            ItemStack itemstack = ItemStack.EMPTY;
            Slot slot = this.slots.get(index);

            if (slot != null && slot.hasItem()) {
                ItemStack slotStack = slot.getItem();
                itemstack = slotStack.copy();

                if (!this.moveItemStackTo(slotStack, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }

                if (slotStack.isEmpty()) {
                    slot.setByPlayer(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }

            return itemstack;
        }

        // Regular player inventory handling
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            // Try to move item filter to filter slots first
            if (slotStack.getItem() == CTItems.ITEM_FILTER.get()) {
                if (!this.moveItemStackTo(slotStack, 0, 2, false)) {
                    // If filter slots full, move within player inventory
                    if (index >= 2 && index < 29) {
                        if (!this.moveItemStackTo(slotStack, 29, 38, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 29 && index < 38) {
                        if (!this.moveItemStackTo(slotStack, 2, 29, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else {
                // Regular items - move within player inventory
                if (index >= 2 && index < 29) {
                    if (!this.moveItemStackTo(slotStack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38) {
                    if (!this.moveItemStackTo(slotStack, 2, 29, false)) {
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
        return crateEntity != null && !crateEntity.isRemoved() &&
                player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
}