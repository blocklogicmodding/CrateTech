package com.blocklogic.cratetech.datagen;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.item.CTItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class CTItemModelProvider extends ItemModelProvider {
    public CTItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CrateTech.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(CTItems.COLLECTOR_UPGRADE.get());
        basicItem(CTItems.HOPPER_UPGRADE.get());
        basicItem(CTItems.SHULKER_UPGRADE.get());
        basicItem(CTItems.COMPACTING_UPGRADE.get());
        basicItem(CTItems.UPGRADE_BASE.get());
        basicItem(CTItems.ITEM_FILTER.get());
        basicItem(CTItems.SMALL_TO_MEDIUM_CRATE_UPGRADE.get());
        basicItem(CTItems.MEDIUM_TO_LARGE_CRATE_UPGRADE.get());
        basicItem(CTItems.LARGE_TO_HUGE_CRATE_UPGRADE.get());
    }
}
