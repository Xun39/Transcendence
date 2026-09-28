package net.xun.transcendence.affix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.Item;

public interface AffixEntry {
    static MapCodec<? extends AffixEntry> codecFor(String type) {
        return switch (type) {
            case "attribute" -> AttributeModifierEntry.CODEC;
            default -> throw new IllegalArgumentException("Unknown affix entry type: " + type);
        };
    }

    Codec<AffixEntry> CODEC = Codec.STRING.dispatch(
            "type",
            AffixEntry::type,
            AffixEntry::codecFor
    );

    String type();
    boolean matches(Item item);
}
