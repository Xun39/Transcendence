package net.xun.transcendence.affix.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record AffixData(List<AffixInstance> affixes, boolean generatedName) {
    public static final Codec<AffixData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    AffixInstance.CODEC.listOf().fieldOf("affixes").forGetter(AffixData::affixes),
                    Codec.BOOL.optionalFieldOf("generated_name", false).forGetter(AffixData::generatedName)
            ).apply(instance, AffixData::new)
    );

    public AffixData {
        affixes = List.copyOf(affixes);
    }

    public static AffixData empty() {
        return new AffixData(List.of(), false);
    }
}