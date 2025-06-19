package com.blocklogic.cratetech.datagen;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.item.CTItems;
import com.blocklogic.cratetech.util.CTTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CTItemTagProvider extends ItemTagsProvider {
    public CTItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CrateTech.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(CTTags.Items.CRATETECH_TIER_UPGRADES)
                .add(CTItems.SMALL_TO_MEDIUM_CRATE_UPGRADE.get())
                .add(CTItems.MEDIUM_TO_LARGE_CRATE_UPGRADE.get())
                .add(CTItems.LARGE_TO_HUGE_CRATE_UPGRADE.get());

        tag(CTTags.Items.CRATETECH_UPGRADES)
                .add(CTItems.COLLECTOR_UPGRADE.get())
                .add(CTItems.HOPPER_UPGRADE.get())
                .add(CTItems.COMPACTING_UPGRADE.get())
                .add(CTItems.SHULKER_UPGRADE.get())
                .add(CTItems.ITEM_FILTER.get())
                .add(CTItems.UPGRADE_BASE.get());
    }
}
