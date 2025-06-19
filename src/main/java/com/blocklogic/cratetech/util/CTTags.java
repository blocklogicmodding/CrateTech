package com.blocklogic.cratetech.util;

import com.blocklogic.cratetech.CrateTech;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CTTags {
    public static class Blocks {
        public static final TagKey<Block> CRATETECH_CRATES = createTag("cratetech_crates");

        private static TagKey<Block> createTag (String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(CrateTech.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> CRATETECH_UPGRADES = createTag("cratetech_upgrades");
        public static final TagKey<Item> CRATETECH_TIER_UPGRADES = createTag("cratetech_tier_upgrades");

        private static TagKey<Item> createTag (String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(CrateTech.MODID, name));
        }
    }
}
