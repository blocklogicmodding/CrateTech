package com.blocklogic.cratetech.block.entity;

import com.blocklogic.cratetech.component.*;
import com.blocklogic.cratetech.item.CTItems;
import com.blocklogic.cratetech.network.CTNetworkHandler;
import com.blocklogic.cratetech.network.SyncCollectorSettingsPacket;
import com.blocklogic.cratetech.network.SyncCompactingSettingsPacket;
import com.blocklogic.cratetech.network.SyncHopperSettingsPacket;
import com.blocklogic.cratetech.screen.custom.CollectorUpgradeMenu;
import com.blocklogic.cratetech.screen.custom.CompactingUpgradeMenu;
import com.blocklogic.cratetech.screen.custom.HopperUpgradeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class BaseCrateBlockEntity extends BlockEntity implements MenuProvider {
    protected final ItemStackHandler itemHandler;
    protected final int inventorySize;

    private CollectorSettings collectorSettings = CollectorSettings.DEFAULT;
    private HopperSettings hopperSettings = HopperSettings.DEFAULT;
    private CompactingSettings compactingSettings = CompactingSettings.DEFAULT;

    public BaseCrateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int size) {
        super(type, pos, blockState);
        this.inventorySize = size;
        this.itemHandler = new ItemStackHandler(size + 2) {
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

    public int getPushFilterSlotIndex() {
        if (inventorySize == 14) return 14;
        if (inventorySize == 32) return 32;
        if (inventorySize == 59) return 59;
        if (inventorySize == 109) return 109;
        return inventorySize;
    }

    public int getPullFilterSlotIndex() {
        if (inventorySize == 14) return 15;
        if (inventorySize == 32) return 33;
        if (inventorySize == 59) return 60;
        if (inventorySize == 109) return 110;
        return inventorySize + 1;
    }

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

    public void adjustCollectionZoneUniform(int change) {
        CollectorSettings current = getCollectorSettings();
        setCollectorSettings(current.withUniformAdjustment(change));
    }

    public void adjustCollectionZone(int direction, int change) {
        CollectorSettings current = getCollectorSettings();
        int currentAdjustment = current.getAdjustment(direction);
        int newAdjustment = Math.max(-3, Math.min(9, currentAdjustment + change));
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

    public HopperSettings getHopperSettings() {
        return hopperSettings;
    }

    public void setHopperSettings(HopperSettings settings) {
        this.hopperSettings = settings;
        setChanged();
    }

    public HopperSettings.SideMode getHopperSideMode(int direction) {
        return getHopperSettings().getSideMode(direction);
    }

    public void toggleHopperSide(int direction) {
        HopperSettings current = getHopperSettings();
        HopperSettings.SideMode currentMode = current.getSideMode(direction);
        HopperSettings.SideMode newMode = current.cycleSideMode(currentMode);
        setHopperSettings(current.withSideMode(direction, newMode));
    }

    public void toggleHopperPushMode() {
        HopperSettings current = getHopperSettings();
        setHopperSettings(current.withPushMode(!current.pushMode()));
    }

    public void toggleHopperPullMode() {
        HopperSettings current = getHopperSettings();
        setHopperSettings(current.withPullMode(!current.pullMode()));
    }

    public boolean isHopperPushModeActive() {
        return getHopperSettings().pushMode();
    }

    public boolean isHopperPullModeActive() {
        return getHopperSettings().pullMode();
    }

    public void resetHopperSettings() {
        setHopperSettings(HopperSettings.DEFAULT);
    }

    public CompactingSettings getCompactingSettings() {
        return compactingSettings;
    }

    public void setCompactingSettings(CompactingSettings settings) {
        this.compactingSettings = settings;
        setChanged();
    }

    public void updateCompactingFilter(List<Item> filterItems) {
        CompactingSettings current = getCompactingSettings();
        setCompactingSettings(current.withFilterItems(filterItems));
    }

    public void toggleCompactingWhitelistMode() {
        CompactingSettings current = getCompactingSettings();
        setCompactingSettings(current.withWhitelistMode(!current.whitelistMode()));
    }

    public void toggleCompacting3x3Mode() {
        CompactingSettings current = getCompactingSettings();
        setCompactingSettings(current.withUse3x3Recipes(!current.use3x3Recipes()));
    }

    public void resetCompactingSettings() {
        setCompactingSettings(CompactingSettings.DEFAULT);
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

        if (!hopperSettings.equals(HopperSettings.DEFAULT)) {
            CompoundTag hopperTag = new CompoundTag();
            hopperTag.putString("up", hopperSettings.up().name());
            hopperTag.putString("down", hopperSettings.down().name());
            hopperTag.putString("north", hopperSettings.north().name());
            hopperTag.putString("south", hopperSettings.south().name());
            hopperTag.putString("east", hopperSettings.east().name());
            hopperTag.putString("west", hopperSettings.west().name());
            hopperTag.putBoolean("push_mode", hopperSettings.pushMode());
            hopperTag.putBoolean("pull_mode", hopperSettings.pullMode());
            tag.put("hopper_settings", hopperTag);
        }

        if (!compactingSettings.equals(CompactingSettings.DEFAULT)) {
            CompoundTag compactingTag = new CompoundTag();
            ListTag filterItemsTag = new ListTag();
            for (Item item : compactingSettings.filterItems()) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                compactingTag.putString("item_" + filterItemsTag.size(), itemId.toString());
                filterItemsTag.add(StringTag.valueOf(itemId.toString()));
            }
            compactingTag.put("filter_items", filterItemsTag);
            compactingTag.putBoolean("whitelist_mode", compactingSettings.whitelistMode());
            compactingTag.putBoolean("use_3x3_recipes", compactingSettings.use3x3Recipes());
            tag.put("compacting_settings", compactingTag);
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

        if (tag.contains("hopper_settings")) {
            CompoundTag hopperTag = tag.getCompound("hopper_settings");
            this.hopperSettings = new HopperSettings(
                    HopperSettings.SideMode.valueOf(hopperTag.getString("up").toUpperCase()),
                    HopperSettings.SideMode.valueOf(hopperTag.getString("down").toUpperCase()),
                    HopperSettings.SideMode.valueOf(hopperTag.getString("north").toUpperCase()),
                    HopperSettings.SideMode.valueOf(hopperTag.getString("south").toUpperCase()),
                    HopperSettings.SideMode.valueOf(hopperTag.getString("east").toUpperCase()),
                    HopperSettings.SideMode.valueOf(hopperTag.getString("west").toUpperCase()),
                    hopperTag.getBoolean("push_mode"),
                    hopperTag.getBoolean("pull_mode")
            );
        } else {
            this.hopperSettings = HopperSettings.DEFAULT;
        }

        if (tag.contains("compacting_settings")) {
            CompoundTag compactingTag = tag.getCompound("compacting_settings");
            List<Item> filterItems = new ArrayList<>();

            if (compactingTag.contains("filter_items")) {
                ListTag filterItemsTag = compactingTag.getList("filter_items", Tag.TAG_STRING);
                for (int i = 0; i < filterItemsTag.size(); i++) {
                    ResourceLocation itemId = ResourceLocation.tryParse(filterItemsTag.getString(i));
                    if (itemId != null) {
                        Item item = BuiltInRegistries.ITEM.get(itemId);
                        if (item != null) {
                            filterItems.add(item);
                        }
                    }
                }
            }

            this.compactingSettings = new CompactingSettings(
                    filterItems,
                    compactingTag.getBoolean("whitelist_mode"),
                    compactingTag.getBoolean("use_3x3_recipes")
            );
        } else {
            this.compactingSettings = CompactingSettings.DEFAULT;
        }

        if (level != null && !level.isClientSide()) {
            syncSettingsToClients();
        }
    }

    private void syncSettingsToClients() {
        if (level == null || level.isClientSide()) return;

        level.players().stream()
                .filter(player -> player instanceof ServerPlayer)
                .map(player -> (ServerPlayer) player)
                .filter(player -> player.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) <= 64 * 64)
                .forEach(player -> {
                    if (!collectorSettings.equals(CollectorSettings.DEFAULT)) {
                        CTNetworkHandler.sendToPlayer(player,
                                new SyncCollectorSettingsPacket(worldPosition, collectorSettings));
                    }
                    if (!hopperSettings.equals(HopperSettings.DEFAULT)) {
                        CTNetworkHandler.sendToPlayer(player,
                                new SyncHopperSettingsPacket(worldPosition, hopperSettings));
                    }
                    if (!compactingSettings.equals(CompactingSettings.DEFAULT)) {
                        CTNetworkHandler.sendToPlayer(player,
                                new SyncCompactingSettingsPacket(worldPosition, compactingSettings));
                    }
                });
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

        if (blockEntity.hasHopperUpgrade()) {
            blockEntity.performHopperOperations(level, pos);
        }

        if (blockEntity.hasCompactingUpgrade()) {
            blockEntity.performCompacting(level, pos);
        }

        if (level.getGameTime() % 100 == 0) {
            blockEntity.syncSettingsToNearbyPlayers();
        }
    }

    private void syncSettingsToNearbyPlayers() {
        if (level == null || level.isClientSide()) return;

        level.players().stream()
                .filter(player -> player instanceof ServerPlayer)
                .map(player -> (ServerPlayer) player)
                .filter(player -> player.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) <= 32 * 32)
                .forEach(player -> {
                    if (player.containerMenu instanceof CollectorUpgradeMenu ||
                            player.containerMenu instanceof HopperUpgradeMenu ||
                            player.containerMenu instanceof CompactingUpgradeMenu) {

                        if (!collectorSettings.equals(CollectorSettings.DEFAULT)) {
                            CTNetworkHandler.sendToPlayer(player,
                                    new SyncCollectorSettingsPacket(worldPosition, collectorSettings));
                        }
                        if (!hopperSettings.equals(HopperSettings.DEFAULT)) {
                            CTNetworkHandler.sendToPlayer(player,
                                    new SyncHopperSettingsPacket(worldPosition, hopperSettings));
                        }
                        if (!compactingSettings.equals(CompactingSettings.DEFAULT)) {
                            CTNetworkHandler.sendToPlayer(player,
                                    new SyncCompactingSettingsPacket(worldPosition, compactingSettings));
                        }
                    }
                });
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

    private boolean hasHopperUpgrade() {
        int upgradeSlotStart = getUpgradeSlotStart();
        for (int i = 0; i < 4; i++) {
            ItemStack stack = itemHandler.getStackInSlot(upgradeSlotStart + i);
            if (!stack.isEmpty() && stack.getItem() == CTItems.HOPPER_UPGRADE.get()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCompactingUpgrade() {
        int upgradeSlotStart = getUpgradeSlotStart();
        for (int i = 0; i < 4; i++) {
            ItemStack stack = itemHandler.getStackInSlot(upgradeSlotStart + i);
            if (!stack.isEmpty() && stack.getItem() == CTItems.COMPACTING_UPGRADE.get()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasShulkerUpgrade() {
        int upgradeSlotStart = getUpgradeSlotStart();
        for (int i = 0; i < 4; i++) {
            ItemStack stack = itemHandler.getStackInSlot(upgradeSlotStart + i);
            if (!stack.isEmpty() && stack.getItem() == CTItems.SHULKER_UPGRADE.get()) {
                return true;
            }
        }
        return false;
    }

    public int getInventorySize() {
        return inventorySize;
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
        int minX = pos.getX() - baseRadius - Math.max(-3, settings.westAdjustment());
        int maxX = pos.getX() + baseRadius + Math.max(-3, settings.eastAdjustment());
        int minY = pos.getY() - baseRadius - Math.max(-3, settings.downAdjustment());
        int maxY = pos.getY() + baseRadius + Math.max(-3, settings.upAdjustment());
        int minZ = pos.getZ() - baseRadius - Math.max(-3, settings.northAdjustment());
        int maxZ = pos.getZ() + baseRadius + Math.max(-3, settings.southAdjustment());

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

        ItemFilterSettings filterSettings = filterStack.get(CTDataComponents.ITEM_FILTER_SETTINGS.get());
        if (filterSettings == null) {
            return true;
        }

        return filterSettings.shouldAllowItem(itemStack);
    }

    private int getFilterSlot() {
        if (inventorySize == 14) return 9;
        if (inventorySize == 32) return 27;
        if (inventorySize == 59) return 54;
        if (inventorySize == 109) return 104;
        return 0;
    }

    private void performHopperOperations(Level level, BlockPos pos) {
        if (level.getGameTime() % 8 != 0) {
            return;
        }

        HopperSettings settings = getHopperSettings();

        if (!settings.pushMode() && !settings.pullMode()) {
            return;
        }

        for (Direction direction : Direction.values()) {
            int dirIndex = switch (direction) {
                case DOWN -> 0;
                case UP -> 1;
                case NORTH -> 2;
                case SOUTH -> 3;
                case WEST -> 4;
                case EAST -> 5;
            };

            HopperSettings.SideMode sideMode = settings.getSideMode(dirIndex);

            if (sideMode == HopperSettings.SideMode.DISABLED) {
                continue;
            }

            BlockPos adjacentPos = pos.relative(direction);
            BlockEntity adjacentEntity = level.getBlockEntity(adjacentPos);

            if (adjacentEntity != null) {
                IItemHandler adjacentHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, adjacentPos, direction.getOpposite());

                if (adjacentHandler != null) {
                    if (sideMode == HopperSettings.SideMode.PUSH && settings.pushMode()) {
                        pushItemsToInventory(adjacentHandler);
                    } else if (sideMode == HopperSettings.SideMode.PULL && settings.pullMode()) {
                        pullItemsFromInventory(adjacentHandler);
                    }
                }
            }
        }
    }

    private void pushItemsToInventory(IItemHandler targetHandler) {
        int storageSlots = getStorageSlotCount();

        ItemStack pushFilterStack = itemHandler.getStackInSlot(getPushFilterSlotIndex());
        ItemFilterSettings pushFilter = null;
        if (!pushFilterStack.isEmpty() && pushFilterStack.getItem() == CTItems.ITEM_FILTER.get()) {
            pushFilter = pushFilterStack.get(CTDataComponents.ITEM_FILTER_SETTINGS.get());
        }

        for (int i = 0; i < storageSlots; i++) {
            ItemStack sourceStack = itemHandler.getStackInSlot(i);
            if (sourceStack.isEmpty()) {
                continue;
            }

            if (pushFilter != null && !pushFilter.shouldAllowItem(sourceStack)) {
                continue;
            }

            ItemStack transferStack = sourceStack.copy();
            transferStack.setCount(1);

            for (int j = 0; j < targetHandler.getSlots(); j++) {
                ItemStack remainder = targetHandler.insertItem(j, transferStack, false);

                if (remainder.isEmpty()) {
                    itemHandler.extractItem(i, 1, false);
                    setChanged();
                    return;
                }
            }
        }
    }

    private void pullItemsFromInventory(IItemHandler sourceHandler) {
        int storageSlots = getStorageSlotCount();

        ItemStack pullFilterStack = itemHandler.getStackInSlot(getPullFilterSlotIndex());
        ItemFilterSettings pullFilter = null;
        if (!pullFilterStack.isEmpty() && pullFilterStack.getItem() == CTItems.ITEM_FILTER.get()) {
            pullFilter = pullFilterStack.get(CTDataComponents.ITEM_FILTER_SETTINGS.get());
        }

        for (int i = 0; i < sourceHandler.getSlots(); i++) {
            ItemStack sourceStack = sourceHandler.getStackInSlot(i);
            if (sourceStack.isEmpty()) {
                continue;
            }

            ItemStack extractStack = sourceHandler.extractItem(i, 1, true);
            if (extractStack.isEmpty()) {
                continue;
            }

            if (pullFilter != null && !pullFilter.shouldAllowItem(extractStack)) {
                continue;
            }

            ItemStack remainder = insertItemIntoCrate(extractStack);

            if (remainder.isEmpty()) {
                sourceHandler.extractItem(i, 1, false);
                setChanged();
                return;
            }
        }
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

    private void performCompacting(Level level, BlockPos pos) {
        if (level.getGameTime() % 40 != 0) {
            return;
        }

        CompactingSettings settings = getCompactingSettings();
        int storageSlots = getStorageSlotCount();

        Map<Item, Integer> itemCounts = new HashMap<>();
        Map<Item, List<Integer>> itemSlots = new HashMap<>();

        for (int i = 0; i < storageSlots; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && settings.shouldCompactItem(stack)) {
                Item item = stack.getItem();
                itemCounts.put(item, itemCounts.getOrDefault(item, 0) + stack.getCount());
                itemSlots.computeIfAbsent(item, k -> new ArrayList<>()).add(i);
            }
        }

        for (Map.Entry<Item, Integer> entry : itemCounts.entrySet()) {
            Item item = entry.getKey();
            int totalCount = entry.getValue();
            List<Integer> slots = itemSlots.get(item);

            if (settings.use3x3Recipes() && totalCount >= 9) {
                ItemStack result = findCompactingRecipe(new ItemStack(item), 3);
                if (!result.isEmpty()) {
                    int craftsToMake = totalCount / 9;
                    craftsToMake = Math.min(craftsToMake, 64 / result.getCount());

                    if (tryInsertItems(result.copyWithCount(result.getCount() * craftsToMake))) {
                        removeItemsFromSlots(slots, item, craftsToMake * 9);
                        continue;
                    }
                }
            }

            if (totalCount >= 4) {
                ItemStack result = findCompactingRecipe(new ItemStack(item), 2);
                if (!result.isEmpty()) {
                    int craftsToMake = totalCount / 4;
                    craftsToMake = Math.min(craftsToMake, 64 / result.getCount());

                    if (tryInsertItems(result.copyWithCount(result.getCount() * craftsToMake))) {
                        removeItemsFromSlots(slots, item, craftsToMake * 4);
                    }
                }
            }
        }
    }

    private ItemStack findCompactingRecipe(ItemStack ingredient, int gridSize) {
        if (level == null) return ItemStack.EMPTY;

        ItemStack[] ingredients = new ItemStack[gridSize * gridSize];
        for (int i = 0; i < ingredients.length; i++) {
            ingredients[i] = ingredient.copy();
            ingredients[i].setCount(1);
        }

        CraftingInput craftingInput = CraftingInput.of(gridSize, gridSize, Arrays.asList(ingredients));

        Optional<RecipeHolder<CraftingRecipe>> recipeHolder = level.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, craftingInput, level);

        if (recipeHolder.isPresent()) {
            CraftingRecipe recipe = recipeHolder.get().value();
            return recipe.assemble(craftingInput, level.registryAccess());
        }

        return ItemStack.EMPTY;
    }

    private boolean tryInsertItems(ItemStack stack) {
        ItemStack remainder = insertItemIntoCrate(stack);
        return remainder.isEmpty();
    }

    private void removeItemsFromSlots(List<Integer> slots, Item item, int totalToRemove) {
        int remaining = totalToRemove;

        for (int slotIndex : slots) {
            if (remaining <= 0) break;

            ItemStack stack = itemHandler.getStackInSlot(slotIndex);
            if (!stack.isEmpty() && stack.getItem() == item) {
                int toRemove = Math.min(remaining, stack.getCount());
                stack.shrink(toRemove);
                remaining -= toRemove;
                setChanged();
            }
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CTBlockEntities.SMALL_CRATE_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof SmallCrateBlockEntity crate) {
                        return crate.getItemHandler(direction);
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CTBlockEntities.MEDIUM_CRATE_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof MediumCrateBlockEntity crate) {
                        return crate.getItemHandler(direction);
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CTBlockEntities.LARGE_CRATE_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof LargeCrateBlockEntity crate) {
                        return crate.getItemHandler(direction);
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CTBlockEntities.HUGE_CRATE_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof HugeCrateBlockEntity crate) {
                        return crate.getItemHandler(direction);
                    }
                    return null;
                });
    }

    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction direction) {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return getStorageSlotCount();
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                if (slot >= getStorageSlotCount()) return ItemStack.EMPTY;
                return itemHandler.getStackInSlot(slot);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot >= getStorageSlotCount()) return stack;
                return itemHandler.insertItem(slot, stack, simulate);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot >= getStorageSlotCount()) return ItemStack.EMPTY;
                return itemHandler.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot >= getStorageSlotCount()) return 0;
                return itemHandler.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (slot >= getStorageSlotCount()) return false;
                return itemHandler.isItemValid(slot, stack);
            }
        };
    }
}