package com.blocklogic.cratetech.screen.custom;

import com.blocklogic.cratetech.block.entity.BaseCrateBlockEntity;
import com.blocklogic.cratetech.network.CTNetworkHandler;
import com.blocklogic.cratetech.network.CollectorAdjustmentPacket;
import com.blocklogic.cratetech.network.CollectorResetPacket;
import com.blocklogic.cratetech.network.CollectorUniformAdjustmentPacket;
import com.blocklogic.cratetech.network.CollectorWireframePacket;
import com.blocklogic.cratetech.screen.CTMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CollectorUpgradeMenu extends AbstractContainerMenu {
    private final BaseCrateBlockEntity crateEntity;
    private final Level level;
    private final BlockPos pos;

    public CollectorUpgradeMenu(int containerId, Inventory playerInventory, BaseCrateBlockEntity crateEntity) {
        super(CTMenuTypes.COLLECTOR_UPGRADE_MENU.get(), containerId);
        this.crateEntity = crateEntity;
        this.level = playerInventory.player.level();
        this.pos = crateEntity.getBlockPos();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 73 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    8 + col * 18, 132));
        }
    }

    public BaseCrateBlockEntity getCrateEntity() {
        return crateEntity;
    }

    public void adjustCollectionZone(int direction, int change) {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new CollectorAdjustmentPacket(pos, direction, change));
        } else {
            crateEntity.adjustCollectionZone(direction, change);
        }
    }

    public void adjustCollectionZoneUniform(int change) {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new CollectorUniformAdjustmentPacket(pos, change));
        } else {
            crateEntity.adjustCollectionZoneUniform(change);
        }
    }

    public void resetCollectionZone() {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new CollectorResetPacket(pos));
        } else {
            crateEntity.resetCollectionZone();
        }
    }

    public void toggleWireframe() {
        if (level.isClientSide()) {
            CTNetworkHandler.sendToServer(new CollectorWireframePacket(pos));
        } else {
            crateEntity.toggleWireframe();
        }
    }

    public int getCollectionAdjustment(int direction) {
        return crateEntity.getCollectionAdjustment(direction);
    }

    public boolean isWireframeVisible() {
        return crateEntity.isWireframeVisible();
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