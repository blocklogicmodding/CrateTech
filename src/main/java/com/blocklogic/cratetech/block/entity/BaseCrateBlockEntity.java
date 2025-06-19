package com.blocklogic.cratetech.block.entity;

import com.blocklogic.cratetech.component.CTDataComponents;
import com.blocklogic.cratetech.component.CollectorSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public abstract class BaseCrateBlockEntity extends BlockEntity implements MenuProvider {
    protected final ItemStackHandler itemHandler;
    protected final int inventorySize;

    public BaseCrateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int size) {
        super(type, pos, blockState);
        this.inventorySize = size;
        this.itemHandler = new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public int getSlotLimit(int slot) {
                if (isFilterOrUpgradeSlot(slot)) {
                    return 1;
                }
                return super.getSlotLimit(slot);
            }
        };
    }

    private boolean isFilterOrUpgradeSlot(int slot) {
        if (inventorySize == 14) {
            return slot >= 9;
        } else if (inventorySize == 32) {
            return slot >= 27;
        } else if (inventorySize == 59) {
            return slot >= 54;
        } else if (inventorySize == 109) {
            return slot >= 104;
        }
        return false;
    }

    // Collector Settings - stored as fields in the block entity
    private CollectorSettings collectorSettings = CollectorSettings.DEFAULT;

    public CollectorSettings getCollectorSettings() {
        return collectorSettings;
    }

    public void setCollectorSettings(CollectorSettings settings) {
        this.collectorSettings = settings;
        setChanged();
    }

    public int getCollectionAdjustment(int direction) {
        return getCollectorSettings().getAdjustment(direction);
    }

    public void adjustCollectionZone(int direction, int change) {
        CollectorSettings current = getCollectorSettings();
        int currentAdjustment = current.getAdjustment(direction);
        int newAdjustment = Math.max(-6, Math.min(9, currentAdjustment + change));
        setCollectorSettings(current.withAdjustment(direction, newAdjustment));
    }

    public void resetCollectionZone() {
        CollectorSettings current = getCollectorSettings();
        setCollectorSettings(current.reset());
    }

    public boolean isWireframeVisible() {
        return getCollectorSettings().wireframeVisible();
    }

    public void toggleWireframe() {
        CollectorSettings current = getCollectorSettings();
        setCollectorSettings(current.withWireframe(!current.wireframeVisible()));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));

        // Save collector settings
        if (!collectorSettings.equals(CollectorSettings.DEFAULT)) {
            CompoundTag collectorTag = new CompoundTag();
            collectorTag.putInt("down", collectorSettings.downAdjustment());
            collectorTag.putInt("up", collectorSettings.upAdjustment());
            collectorTag.putInt("north", collectorSettings.northAdjustment());
            collectorTag.putInt("south", collectorSettings.southAdjustment());
            collectorTag.putInt("west", collectorSettings.westAdjustment());
            collectorTag.putInt("east", collectorSettings.eastAdjustment());
            collectorTag.putBoolean("wireframe", collectorSettings.wireframeVisible());
            tag.put("collector_settings", collectorTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }

        // Load collector settings
        if (tag.contains("collector_settings")) {
            CompoundTag collectorTag = tag.getCompound("collector_settings");
            this.collectorSettings = new CollectorSettings(
                    collectorTag.getInt("down"),
                    collectorTag.getInt("up"),
                    collectorTag.getInt("north"),
                    collectorTag.getInt("south"),
                    collectorTag.getInt("west"),
                    collectorTag.getInt("east"),
                    collectorTag.getBoolean("wireframe")
            );
        } else {
            this.collectorSettings = CollectorSettings.DEFAULT;
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        NonNullList<ItemStack> items = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
        for (int i = 0; i < inventorySize; i++) {
            items.set(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, pos, items);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public Component getDisplayName() {
        return getDefaultName();
    }

    protected abstract Component getDefaultName();
    protected abstract MenuType<?> getMenuType();
    protected abstract int getMenuRows();

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ChestMenu(getMenuType(), containerId, playerInventory,
                new ItemStackHandlerContainer(itemHandler), getMenuRows());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BaseCrateBlockEntity blockEntity) {
        // TODO: Add upgrade module processing here
        // This will handle collector upgrades, hopper upgrades, compacting upgrades, etc.

        // Example collector logic:
        // if (blockEntity.hasCollectorUpgrade()) {
        //     blockEntity.performCollection(level, pos);
        // }
    }

    private static class ItemStackHandlerContainer implements net.minecraft.world.Container {
        private final ItemStackHandler handler;

        public ItemStackHandlerContainer(ItemStackHandler handler) {
            this.handler = handler;
        }

        @Override
        public int getContainerSize() {
            return handler.getSlots();
        }

        @Override
        public boolean isEmpty() {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (!handler.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return handler.getStackInSlot(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return handler.extractItem(slot, amount, false);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack stack = handler.getStackInSlot(slot).copy();
            handler.setStackInSlot(slot, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            handler.setStackInSlot(slot, stack);
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
            for (int i = 0; i < handler.getSlots(); i++) {
                handler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }
}