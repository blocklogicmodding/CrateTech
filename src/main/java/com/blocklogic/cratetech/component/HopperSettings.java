package com.blocklogic.cratetech.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record HopperSettings(
        SideMode up,
        SideMode down,
        SideMode north,
        SideMode south,
        SideMode east,
        SideMode west,
        boolean pushMode,
        boolean pullMode
) {
    public static final HopperSettings DEFAULT = new HopperSettings(
            SideMode.DISABLED, SideMode.DISABLED, SideMode.DISABLED,
            SideMode.DISABLED, SideMode.DISABLED, SideMode.DISABLED,
            false, false
    );

    public enum SideMode {
        DISABLED, PUSH, PULL
    }

    public static final Codec<SideMode> SIDE_MODE_CODEC = Codec.STRING.xmap(
            name -> SideMode.valueOf(name.toUpperCase()),
            mode -> mode.name().toLowerCase()
    );

    public static final Codec<HopperSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    SIDE_MODE_CODEC.fieldOf("up").forGetter(HopperSettings::up),
                    SIDE_MODE_CODEC.fieldOf("down").forGetter(HopperSettings::down),
                    SIDE_MODE_CODEC.fieldOf("north").forGetter(HopperSettings::north),
                    SIDE_MODE_CODEC.fieldOf("south").forGetter(HopperSettings::south),
                    SIDE_MODE_CODEC.fieldOf("east").forGetter(HopperSettings::east),
                    SIDE_MODE_CODEC.fieldOf("west").forGetter(HopperSettings::west),
                    Codec.BOOL.fieldOf("push_mode").forGetter(HopperSettings::pushMode),
                    Codec.BOOL.fieldOf("pull_mode").forGetter(HopperSettings::pullMode)
            ).apply(instance, HopperSettings::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HopperSettings> STREAM_CODEC =
            StreamCodec.of(HopperSettings::encode, HopperSettings::decode);

    private static void encode(RegistryFriendlyByteBuf buf, HopperSettings settings) {
        buf.writeEnum(settings.up);
        buf.writeEnum(settings.down);
        buf.writeEnum(settings.north);
        buf.writeEnum(settings.south);
        buf.writeEnum(settings.east);
        buf.writeEnum(settings.west);
        buf.writeBoolean(settings.pushMode);
        buf.writeBoolean(settings.pullMode);
    }

    private static HopperSettings decode(RegistryFriendlyByteBuf buf) {
        return new HopperSettings(
                buf.readEnum(SideMode.class),
                buf.readEnum(SideMode.class),
                buf.readEnum(SideMode.class),
                buf.readEnum(SideMode.class),
                buf.readEnum(SideMode.class),
                buf.readEnum(SideMode.class),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public SideMode getSideMode(int direction) {
        return switch (direction) {
            case 0 -> down;
            case 1 -> up;
            case 2 -> north;
            case 3 -> south;
            case 4 -> west;
            case 5 -> east;
            default -> SideMode.DISABLED;
        };
    }

    public HopperSettings withSideMode(int direction, SideMode mode) {
        return switch (direction) {
            case 0 -> new HopperSettings(up, mode, north, south, east, west, pushMode, pullMode);
            case 1 -> new HopperSettings(mode, down, north, south, east, west, pushMode, pullMode);
            case 2 -> new HopperSettings(up, down, mode, south, east, west, pushMode, pullMode);
            case 3 -> new HopperSettings(up, down, north, mode, east, west, pushMode, pullMode);
            case 4 -> new HopperSettings(up, down, north, south, east, mode, pushMode, pullMode);
            case 5 -> new HopperSettings(up, down, north, south, mode, west, pushMode, pullMode);
            default -> this;
        };
    }

    public HopperSettings withPushMode(boolean push) {
        return new HopperSettings(up, down, north, south, east, west, push, pullMode);
    }

    public HopperSettings withPullMode(boolean pull) {
        return new HopperSettings(up, down, north, south, east, west, pushMode, pull);
    }

    public HopperSettings reset() {
        return DEFAULT;
    }

    public SideMode cycleSideMode(SideMode current) {
        return switch (current) {
            case DISABLED -> SideMode.PUSH;
            case PUSH -> SideMode.PULL;
            case PULL -> SideMode.DISABLED;
        };
    }
}