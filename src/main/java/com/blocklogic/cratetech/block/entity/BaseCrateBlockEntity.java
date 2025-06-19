package com.blocklogic.cratetech.block.entity;

import com.blocklogic.cratetech.component.CollectorSettings;
import com.blocklogic.cratetech.item.CTItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        return createCustomMenu(containerId, playerInventory, player);
    }

    public abstract AbstractContainerMenu createCustomMenu(int containerId, Inventory playerInventory, Player player);

    public static void serverTick(Level level, BlockPos pos, BlockState state, BaseCrateBlockEntity blockEntity) {
        if (blockEntity.hasCollectorUpgrade()) {
            blockEntity.performCollection(level, pos);
        }
    }

    private boolean hasCollectorUpgrade() {
        int upgradeSlotStart = getUpgradeSlotStart();
        for (int i = 0; i < 4; i++) {
            ItemStack stack = itemHandler.getStackInSlot(upgradeSlotStart + i);
            if (!stack.isEmpty() && stack.getItem() == CTItems.COLLECTOR_UPGRADE.get()) {
                return true;
            }
        }
        return false;
    }

    private int getUpgradeSlotStart() {
        if (inventorySize == 14) return 10;
        if (inventorySize == 32) return 28;
        if (inventorySize == 59) return 55;
        if (inventorySize == 109) return 105;
        return 0;
    }

    private void performCollection(Level level, BlockPos pos) {
        if (level.getGameTime() % 20 != 0) {
            return;
        }

        CollectorSettings settings = getCollectorSettings();

        int baseRadius = 3;
        int minX = pos.getX() - baseRadius - settings.westAdjustment();
        int maxX = pos.getX() + baseRadius + settings.eastAdjustment();
        int minY = pos.getY() - baseRadius - settings.downAdjustment();
        int maxY = pos.getY() + baseRadius + settings.upAdjustment();
        int minZ = pos.getZ() - baseRadius - settings.northAdjustment();
        int maxZ = pos.getZ() + baseRadius + settings.southAdjustment();

        AABB collectionArea = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);

        List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, collectionArea);

        for (ItemEntity itemEntity : itemEntities) {
            if (itemEntity.isRemoved() || !itemEntity.isAlive()) {
                continue;
            }

            ItemStack itemStack = itemEntity.getItem();

            if (!passesFilter(itemStack)) {
                continue;
            }

            ItemStack remainder = insertItemIntoCrate(itemStack);

            if (remainder.isEmpty()) {
                itemEntity.discard();
            } else if (remainder.getCount() < itemStack.getCount()) {
                itemEntity.setItem(remainder);
            }
        }
    }

    private boolean passesFilter(ItemStack itemStack) {
        int filterSlot = getFilterSlot();
        ItemStack filterStack = itemHandler.getStackInSlot(filterSlot);

        if (filterStack.isEmpty() || !(filterStack.getItem() == CTItems.ITEM_FILTER.get())) {
            return true;
        }

        return true;
    }

    private int getFilterSlot() {
        if (inventorySize == 14) return 9;
        if (inventorySize == 32) return 27;
        if (inventorySize == 59) return 54;
        if (inventorySize == 109) return 104;
        return 0;
    }

    private ItemStack insertItemIntoCrate(ItemStack itemStack) {
        int storageSlots = getStorageSlotCount();

        ItemStack remaining = itemStack.copy();

        for (int i = 0; i < storageSlots; i++) {
            ItemStack slotStack = itemHandler.getStackInSlot(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, remaining)) {
                int maxStackSize = Math.min(slotStack.getMaxStackSize(), itemHandler.getSlotLimit(i));
                int canInsert = maxStackSize - slotStack.getCount();
                if (canInsert > 0) {
                    int toInsert = Math.min(canInsert, remaining.getCount());
                    slotStack.grow(toInsert);
                    remaining.shrink(toInsert);
                    setChanged();

                    if (remaining.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        for (int i = 0; i < storageSlots; i++) {
            ItemStack slotStack = itemHandler.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                int maxStackSize = Math.min(remaining.getMaxStackSize(), itemHandler.getSlotLimit(i));
                int toInsert = Math.min(maxStackSize, remaining.getCount());

                ItemStack insertStack = remaining.copy();
                insertStack.setCount(toInsert);
                itemHandler.setStackInSlot(i, insertStack);
                remaining.shrink(toInsert);
                setChanged();

                if (remaining.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return remaining;
    }

    private int getStorageSlotCount() {
        if (inventorySize == 14) return 9;
        if (inventorySize == 32) return 27;
        if (inventorySize == 59) return 54;
        if (inventorySize == 109) return 104;
        return 0;
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