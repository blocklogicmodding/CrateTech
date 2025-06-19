package com.blocklogic.cratetech.screen.custom;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.component.HopperSettings;
import com.blocklogic.cratetech.network.CTNetworkHandler;
import com.blocklogic.cratetech.network.HopperSideConfigPacket;
import com.blocklogic.cratetech.network.HopperModeTogglePacket;
import com.blocklogic.cratetech.network.HopperResetPacket;
import com.blocklogic.cratetech.screen.CTMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HopperUpgradeMenu extends AbstractContainerMenu {
    private final BaseCrateBlockEntity crateEntity;
    private final Level level;
    private final BlockPos pos;

    public HopperUpgradeMenu(int containerId, Inventory playerInventory, BaseCrateBlockEntity crateEntity) {
        super(CTMenuTypes.HOPPER_UPGRADE_MENU.get(), containerId);
        this.crateEntity = crateEntity;
        this.level = playerInventory.player.level();
        this.pos = crateEntity.getBlockPos();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 86 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    8 + col * 18, 145));
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
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return crateEntity != null && !crateEntity.isRemoved() &&
                player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
}