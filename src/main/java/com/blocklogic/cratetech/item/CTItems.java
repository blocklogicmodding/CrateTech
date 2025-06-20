package com.blocklogic.cratetech.item;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.item.custom.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CTItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CrateTech.MODID);

    public static final DeferredItem<Item> COLLECTOR_UPGRADE = ITEMS.register("collector_upgrade",
            () -> new CollectorUpgradeItem(new Item.Properties()));

    public static final DeferredItem<Item> COMPACTING_UPGRADE = ITEMS.register("compacting_upgrade",
            () -> new CompactingUpgradeItem(new Item.Properties()));

    public static final DeferredItem<Item> HOPPER_UPGRADE = ITEMS.register("hopper_upgrade",
            () -> new HopperUpgradeItem(new Item.Properties()));

    public static final DeferredItem<Item> SHULKER_UPGRADE = ITEMS.register("shulker_upgrade",
            () -> new ShulkerUpgradeItem(new Item.Properties()));

    public static final DeferredItem<Item> UPGRADE_BASE = ITEMS.register("upgrade_base",
            () -> new UpgradeBaseItem(new Item.Properties()));

    public static final DeferredItem<Item> ITEM_FILTER = ITEMS.register("item_filter",
            () -> new ItemFilterItem(new Item.Properties()));

    public static final DeferredItem<CrateTierUpgradeItem> SMALL_TO_MEDIUM_CRATE_UPGRADE = ITEMS.register("small_to_medium_crate_upgrade",
            () -> new CrateTierUpgradeItem(new Item.Properties(), "Small", "Medium", 9, 27));

    public static final DeferredItem<CrateTierUpgradeItem> MEDIUM_TO_LARGE_CRATE_UPGRADE = ITEMS.register("medium_to_large_crate_upgrade",
            () -> new CrateTierUpgradeItem(new Item.Properties(), "Medium", "Large", 27, 54));

    public static final DeferredItem<CrateTierUpgradeItem> LARGE_TO_HUGE_CRATE_UPGRADE = ITEMS.register("large_to_medium_crate_upgrade",
            () -> new CrateTierUpgradeItem(new Item.Properties(), "Large", "Huge", 54, 104));

    public static void register (IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
