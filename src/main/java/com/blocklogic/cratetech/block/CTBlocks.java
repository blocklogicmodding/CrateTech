package com.blocklogic.cratetech.block;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.block.custom.HugeCrateBlock;
import com.blocklogic.cratetech.block.custom.LargeCrateBlock;
import com.blocklogic.cratetech.block.custom.MediumCrateBlock;
import com.blocklogic.cratetech.block.custom.SmallCrateBlock;
import com.blocklogic.cratetech.item.CTItems;
import com.blocklogic.cratetech.item.custom.CrateBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CTBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CrateTech.MODID);

    public static final DeferredBlock<Block> SMALL_CRATE = registerBlock("small_crate",
            () -> new SmallCrateBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> MEDIUM_CRATE = registerBlock("medium_crate",
            () -> new MediumCrateBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> LARGE_CRATE = registerBlock("large_crate",
            () -> new LargeCrateBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> HUGE_CRATE = registerBlock("huge_crate",
            () -> new HugeCrateBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerCrateBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerCrateBlockItem(String name, DeferredBlock<T> block) {
        CTItems.ITEMS.register(name, () -> new CrateBlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
