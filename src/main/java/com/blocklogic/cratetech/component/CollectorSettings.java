package com.blocklogic.cratetech.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CollectorSettings(
        int downAdjustment,
        int upAdjustment,
        int northAdjustment,
        int southAdjustment,
        int westAdjustment,
        int eastAdjustment,
        boolean wireframeVisible
) {
    public static final CollectorSettings DEFAULT = new CollectorSettings(0, 0, 0, 0, 0, 0, false);

    public static final Codec<CollectorSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("down").forGetter(CollectorSettings::downAdjustment),
                    Codec.INT.fieldOf("up").forGetter(CollectorSettings::upAdjustment),
                    Codec.INT.fieldOf("north").forGetter(CollectorSettings::northAdjustment),
                    Codec.INT.fieldOf("south").forGetter(CollectorSettings::southAdjustment),
                    Codec.INT.fieldOf("west").forGetter(CollectorSettings::westAdjustment),
                    Codec.INT.fieldOf("east").forGetter(CollectorSettings::eastAdjustment),
                    Codec.BOOL.fieldOf("wireframe").forGetter(CollectorSettings::wireframeVisible)
            ).apply(instance, CollectorSettings::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CollectorSettings> STREAM_CODEC =
            StreamCodec.of(CollectorSettings::encode, CollectorSettings::decode);

    private static void encode(RegistryFriendlyByteBuf buf, CollectorSettings settings) {
        buf.writeVarInt(settings.downAdjustment);
        buf.writeVarInt(settings.upAdjustment);
        buf.writeVarInt(settings.northAdjustment);
        buf.writeVarInt(settings.southAdjustment);
        buf.writeVarInt(settings.westAdjustment);
        buf.writeVarInt(settings.eastAdjustment);
        buf.writeBoolean(settings.wireframeVisible);
    }

    private static CollectorSettings decode(RegistryFriendlyByteBuf buf) {
        return new CollectorSettings(
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readBoolean()
        );
    }

    public int getAdjustment(int direction) {
        return switch (direction) {
            case 0 -> downAdjustment;
            case 1 -> upAdjustment;
            case 2 -> northAdjustment;
            case 3 -> southAdjustment;
            case 4 -> westAdjustment;
            case 5 -> eastAdjustment;
            default -> 0;
        };
    }

    public CollectorSettings withAdjustment(int direction, int adjustment) {
        adjustment = Math.max(-6, Math.min(9, adjustment));

        return switch (direction) {
            case 0 -> new CollectorSettings(adjustment, upAdjustment, northAdjustment, southAdjustment, westAdjustment, eastAdjustment, wireframeVisible);
            case 1 -> new CollectorSettings(downAdjustment, adjustment, northAdjustment, southAdjustment, westAdjustment, eastAdjustment, wireframeVisible);
            case 2 -> new CollectorSettings(downAdjustment, upAdjustment, adjustment, southAdjustment, westAdjustment, eastAdjustment, wireframeVisible);
            case 3 -> new CollectorSettings(downAdjustment, upAdjustment, northAdjustment, adjustment, westAdjustment, eastAdjustment, wireframeVisible);
            case 4 -> new CollectorSettings(downAdjustment, upAdjustment, northAdjustment, southAdjustment, adjustment, eastAdjustment, wireframeVisible);
            case 5 -> new CollectorSettings(downAdjustment, upAdjustment, northAdjustment, southAdjustment, westAdjustment, adjustment, wireframeVisible);
            default -> this;
        };
    }

    public CollectorSettings withWireframe(boolean wireframe) {
        return new CollectorSettings(downAdjustment, upAdjustment, northAdjustment, southAdjustment, westAdjustment, eastAdjustment, wireframe);
    }

    public CollectorSettings reset() {
        return new CollectorSettings(0, 0, 0, 0, 0, 0, wireframeVisible);
    }
}