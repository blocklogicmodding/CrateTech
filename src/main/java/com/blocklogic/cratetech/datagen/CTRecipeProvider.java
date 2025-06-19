package com.blocklogic.cratetech.datagen;

import com.blocklogic.cratetech.block.CTBlocks;
import com.blocklogic.cratetech.item.CTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class CTRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public CTRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Crate recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTBlocks.SMALL_CRATE.get())
                .pattern("PPP")
                .pattern("PCP")
                .pattern("PPP")
                .define('P', ItemTags.PLANKS)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTBlocks.MEDIUM_CRATE.get())
                .pattern("PIP")
                .pattern("PCP")
                .pattern("PIP")
                .define('P', ItemTags.PLANKS)
                .define('C', CTBlocks.SMALL_CRATE.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_small_crate", has(CTBlocks.SMALL_CRATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTBlocks.LARGE_CRATE.get())
                .pattern("PGP")
                .pattern("PCP")
                .pattern("PGP")
                .define('P', ItemTags.PLANKS)
                .define('C', CTBlocks.MEDIUM_CRATE.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_medium_crate", has(CTBlocks.MEDIUM_CRATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTBlocks.HUGE_CRATE.get())
                .pattern("PDP")
                .pattern("PCP")
                .pattern("PDP")
                .define('P', ItemTags.PLANKS)
                .define('C', CTBlocks.LARGE_CRATE.get())
                .define('D', Items.DIAMOND)
                .unlockedBy("has_large_crate", has(CTBlocks.LARGE_CRATE.get()))
                .save(recipeOutput);

        // Module recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTItems.UPGRADE_BASE.get())
                .pattern("LP ")
                .pattern("PC ")
                .pattern("   ")
                .define('P', ItemTags.PLANKS)
                .define('L', ItemTags.LOGS)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTItems.COLLECTOR_UPGRADE.get())
                .pattern("ER ")
                .pattern("YU ")
                .pattern("   ")
                .define('R', Items.REDSTONE)
                .define('U', CTItems.UPGRADE_BASE.get())
                .define('E', Items.ENDER_PEARL)
                .define('Y', Items.ENDER_EYE)
                .unlockedBy("has_upgrade_base", has(CTItems.UPGRADE_BASE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTItems.SHULKER_UPGRADE.get())
                .pattern("SE ")
                .pattern("CU ")
                .pattern("   ")
                .define('E', Items.ENDER_PEARL)
                .define('S', Items.SHULKER_SHELL)
                .define('C', Tags.Items.CHESTS)
                .define('U', CTItems.UPGRADE_BASE.get())
                .unlockedBy("has_shulker_shell", has(Items.SHULKER_SHELL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTItems.HOPPER_UPGRADE.get())
                .pattern("HI ")
                .pattern("IU ")
                .pattern("   ")
                .define('I', Items.IRON_INGOT)
                .define('H', Items.HOPPER)
                .define('U', CTItems.UPGRADE_BASE.get())
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTItems.COMPACTING_UPGRADE.get())
                .pattern("PB ")
                .pattern("BU ")
                .pattern("   ")
                .define('P', Items.PISTON)
                .define('B', Items.CRAFTING_TABLE)
                .define('U', CTItems.UPGRADE_BASE.get())
                .unlockedBy("has_piston", has(Items.PISTON))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTItems.ITEM_FILTER.get())
                .pattern("IPI")
                .pattern("PUP")
                .pattern("IPI")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PAPER)
                .define('U', CTItems.UPGRADE_BASE.get())
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(recipeOutput);

        // Upgrade item recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTItems.SMALL_TO_MEDIUM_CRATE_UPGRADE.get())
                .pattern("PIP")
                .pattern("PCP")
                .pattern("PIP")
                .define('P', ItemTags.PLANKS)
                .define('C', CTBlocks.SMALL_CRATE.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_small_crate", has(CTBlocks.SMALL_CRATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTItems.MEDIUM_TO_LARGE_CRATE_UPGRADE.get())
                .pattern("PGP")
                .pattern("PCP")
                .pattern("PGP")
                .define('P', ItemTags.PLANKS)
                .define('C', CTBlocks.MEDIUM_CRATE.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_medium_crate", has(CTBlocks.MEDIUM_CRATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CTItems.LARGE_TO_HUGE_CRATE_UPGRADE.get())
                .pattern("PDP")
                .pattern("PCP")
                .pattern("PDP")
                .define('P', ItemTags.PLANKS)
                .define('C', CTBlocks.LARGE_CRATE.get())
                .define('D', Items.DIAMOND)
                .unlockedBy("has_large_crate", has(CTBlocks.LARGE_CRATE.get()))
                .save(recipeOutput);
    }
}