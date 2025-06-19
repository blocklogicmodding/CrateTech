package com.blocklogic.cratetech.datagen;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.block.CTBlocks;
import com.blocklogic.cratetech.util.CTTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CTBlockTagProvider extends BlockTagsProvider {
    public CTBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CrateTech.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(CTBlocks.SMALL_CRATE.get())
                .add(CTBlocks.MEDIUM_CRATE.get())
                .add(CTBlocks.LARGE_CRATE.get())
                .add(CTBlocks.HUGE_CRATE.get());

        tag(CTTags.Blocks.CRATETECH_CRATES)
                .add(CTBlocks.SMALL_CRATE.get())
                .add(CTBlocks.MEDIUM_CRATE.get())
                .add(CTBlocks.LARGE_CRATE.get())
                .add(CTBlocks.HUGE_CRATE.get());
    }
}
