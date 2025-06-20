package com.blocklogic.cratetech.component;

import com.blocklogic.cratetech.CrateTech;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CTDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CrateTech.MODID);

    public static final Supplier<DataComponentType<CollectorSettings>> COLLECTOR_SETTINGS =
            DATA_COMPONENT_TYPES.register("collector_settings", () ->
                    DataComponentType.<CollectorSettings>builder()
                            .persistent(CollectorSettings.CODEC)
                            .networkSynchronized(CollectorSettings.STREAM_CODEC)
                            .build());

    public static final Supplier<DataComponentType<HopperSettings>> HOPPER_SETTINGS =
            DATA_COMPONENT_TYPES.register("hopper_settings", () ->
                    DataComponentType.<HopperSettings>builder()
                            .persistent(HopperSettings.CODEC)
                            .networkSynchronized(HopperSettings.STREAM_CODEC)
                            .build());

    public static final Supplier<DataComponentType<CompactingSettings>> COMPACTING_SETTINGS =
            DATA_COMPONENT_TYPES.register("compacting_settings", () ->
                    DataComponentType.<CompactingSettings>builder()
                            .persistent(CompactingSettings.CODEC)
                            .networkSynchronized(CompactingSettings.STREAM_CODEC)
                            .build());

    public static final Supplier<DataComponentType<CrateContents>> CRATE_CONTENTS =
            DATA_COMPONENT_TYPES.register("crate_contents", () ->
                    DataComponentType.<CrateContents>builder()
                            .persistent(CrateContents.CODEC)
                            .networkSynchronized(CrateContents.STREAM_CODEC)
                            .build());

    public static final Supplier<DataComponentType<ItemFilterSettings>> ITEM_FILTER_SETTINGS =
            DATA_COMPONENT_TYPES.register("item_filter_settings", () ->
                    DataComponentType.<ItemFilterSettings>builder()
                            .persistent(ItemFilterSettings.CODEC)
                            .networkSynchronized(ItemFilterSettings.STREAM_CODEC)
                            .build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}