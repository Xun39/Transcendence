package net.xun.transcendence.affix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;

public enum AffixType {
    PREFIX("prefix"),
    SUFFIX("suffix");

    public static final Codec<AffixType> CODEC = Codec.STRING.comapFlatMap(
            value -> switch (value) {
                case "transcendence:prefix", "prefix" -> DataResult.success(PREFIX);
                case "transcendence:suffix", "suffix" -> DataResult.success(SUFFIX);
                default -> DataResult.error(() -> "Unknown affix type: " + value);
            },
            AffixType::serializedName
    );

    private final String path;

    AffixType(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    public String serializedName() {
        return "transcendence:" + path;
    }

    public ResourceLocation makeId(ResourceLocation id) {
        return id;
    }

    public static AffixType parse(String id) {
        return switch (id) {
            case "transcendence:prefix" -> PREFIX;
            case "transcendence:suffix" -> SUFFIX;
            default -> throw new IllegalArgumentException("Unknown affix type: " + id);
        };
    }
}
