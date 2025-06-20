package com.blocklogic.cratetech.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record ItemFilterSettings(
        List<Item> filterItems,
        boolean whitelistMode,
        boolean matchTags
) {
    public static final ItemFilterSettings DEFAULT = new ItemFilterSettings(
            new ArrayList<>(), true, false
    );

    public static final Codec<ItemFilterSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(BuiltInRegistries.ITEM.byNameCodec()).fieldOf("filter_items").forGetter(ItemFilterSettings::filterItems),
                    Codec.BOOL.fieldOf("whitelist_mode").forGetter(ItemFilterSettings::whitelistMode),
                    Codec.BOOL.fieldOf("match_tags").forGetter(ItemFilterSettings::matchTags)
            ).apply(instance, ItemFilterSettings::new)
    );

    public static final StreamCodec<FriendlyByteBuf, ItemFilterSettings> STREAM_CODEC =
            StreamCodec.of(ItemFilterSettings::encode, ItemFilterSettings::decode);

    private static void encode(FriendlyByteBuf buf, ItemFilterSettings settings) {
        buf.writeVarInt(settings.filterItems.size());
        for (Item item : settings.filterItems) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            buf.writeResourceLocation(itemId);
        }
        buf.writeBoolean(settings.whitelistMode);
        buf.writeBoolean(settings.matchTags);
    }

    private static ItemFilterSettings decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation itemId = buf.readResourceLocation();
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item != null) {
                items.add(item);
            }
        }
        return new ItemFilterSettings(
                items,
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public ItemFilterSettings withFilterItems(List<Item> items) {
        return new ItemFilterSettings(new ArrayList<>(items), whitelistMode, matchTags);
    }

    public ItemFilterSettings withWhitelistMode(boolean whitelist) {
        return new ItemFilterSettings(new ArrayList<>(filterItems), whitelist, matchTags);
    }

    public ItemFilterSettings withMatchTags(boolean matchTags) {
        return new ItemFilterSettings(new ArrayList<>(filterItems), whitelistMode, matchTags);
    }

    public boolean shouldAllowItem(ItemStack itemStack) {
        if (filterItems.isEmpty()) {
            return true; // No filter = allow everything
        }

        boolean matches = false;

        if (matchTags) {
            // Check if any filter item shares tags with the tested item
            for (Item filterItem : filterItems) {
                if (sharesTags(new ItemStack(filterItem), itemStack)) {
                    matches = true;
                    break;
                }
            }
        } else {
            // Exact item matching
            matches = filterItems.contains(itemStack.getItem());
        }

        return whitelistMode ? matches : !matches;
    }

    private boolean sharesTags(ItemStack filterStack, ItemStack testStack) {
        Set<TagKey<Item>> filterTags = filterStack.getTags().collect(java.util.stream.Collectors.toSet());
        Set<TagKey<Item>> testTags = testStack.getTags().collect(java.util.stream.Collectors.toSet());

        for (TagKey<Item> filterTag : filterTags) {
            if (testTags.contains(filterTag)) {
                return true;
            }
        }
        return false;
    }

    public ItemFilterSettings reset() {
        return new ItemFilterSettings(new ArrayList<>(), true, false);
    }
}