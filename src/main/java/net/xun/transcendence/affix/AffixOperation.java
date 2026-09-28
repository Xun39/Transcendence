package net.xun.transcendence.affix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum AffixOperation {
    MODIFY_ATTRIBUTE("transcendence:modify_attribute");

    public static final Codec<AffixOperation> CODEC = Codec.STRING.comapFlatMap(
            value -> switch (value) {
                case "transcendence:modify_attribute", "modify_attribute" -> DataResult.success(MODIFY_ATTRIBUTE);
                default -> DataResult.error(() -> "Unknown affix operation: " + value);
            },
            AffixOperation::serializedName
    );

    private final String serializedName;

    AffixOperation(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return serializedName;
    }
}
