package com.blocklogic.cratetech.block.entity;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.block.CTBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CTBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CrateTech.MODID);

    public static final Supplier<BlockEntityType<SmallCrateBlockEntity>> SMALL_CRATE_BE =
            BLOCK_ENTITIES.register("small_crate_be", () ->
                    BlockEntityType.Builder.of(SmallCrateBlockEntity::new,
                            CTBlocks.SMALL_CRATE.get()).build(null));

    public static final Supplier<BlockEntityType<MediumCrateBlockEntity>> MEDIUM_CRATE_BE =
            BLOCK_ENTITIES.register("medium_crate_be", () ->
                    BlockEntityType.Builder.of(MediumCrateBlockEntity::new,
                            CTBlocks.MEDIUM_CRATE.get()).build(null));

    public static final Supplier<BlockEntityType<LargeCrateBlockEntity>> LARGE_CRATE_BE =
            BLOCK_ENTITIES.register("large_crate_be", () ->
                    BlockEntityType.Builder.of(LargeCrateBlockEntity::new,
                            CTBlocks.LARGE_CRATE.get()).build(null));

    public static final Supplier<BlockEntityType<HugeCrateBlockEntity>> HUGE_CRATE_BE =
            BLOCK_ENTITIES.register("huge_crate_be", () ->
                    BlockEntityType.Builder.of(HugeCrateBlockEntity::new,
                            CTBlocks.HUGE_CRATE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}