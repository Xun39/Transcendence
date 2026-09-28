package net.xun.transcendence.affix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Affix(AffixType type, AffixOperation operation, int weight, List<AffixEntry> entries) {
    public static final Codec<Affix> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    AffixType.CODEC.fieldOf("type").forGetter(Affix::type),
                    AffixOperation.CODEC.fieldOf("operation").forGetter(Affix::operation),
                    Codec.intRange(0, Integer.MAX_VALUE).fieldOf("weight").forGetter(Affix::weight),
                    AffixEntry.CODEC.listOf().fieldOf("entries").forGetter(Affix::entries)
            ).apply(instance, Affix::new)
    );

    public static final class Builder {
        private final AffixType type;
        private final AffixOperation operation;
        private final int weight;
        private final List<AffixEntry> entries = new ArrayList<>();

        private Builder(AffixType type, AffixOperation operation, int weight) {
            this.type = Objects.requireNonNull(type, "type");
            this.operation = Objects.requireNonNull(operation, "operation");
            this.weight = weight;
        }

        public static Builder prefix(AffixOperation operation, int weight) {
            return new Builder(AffixType.PREFIX, operation, weight);
        }

        public static Builder suffix(AffixOperation operation, int weight) {
            return new Builder(AffixType.SUFFIX, operation, weight);
        }

        public Builder entry(AffixEntry entry) {
            entries.add(entry);
            return this;
        }

        public Affix build() {
            if (entries.isEmpty()) {
                throw new IllegalStateException("An affix must have at least one entry");
            }
            return new Affix(type, operation, weight, entries);
        }
    }
}
