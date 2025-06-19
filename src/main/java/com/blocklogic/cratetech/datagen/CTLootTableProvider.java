package com.blocklogic.cratetech.datagen;

import com.blocklogic.cratetech.block.CTBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class CTLootTableProvider extends BlockLootSubProvider {
    protected CTLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(CTBlocks.SMALL_CRATE.get());
        dropSelf(CTBlocks.MEDIUM_CRATE.get());
        dropSelf(CTBlocks.LARGE_CRATE.get());
        dropSelf(CTBlocks.HUGE_CRATE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return CTBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
