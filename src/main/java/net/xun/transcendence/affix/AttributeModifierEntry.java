package net.xun.transcendence.affix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record AttributeModifierEntry(
        ResourceLocation attribute,
        AttributeModifier.Operation operation,
        AmountRange amount,
        EquipmentSlotGroup slot,
        List<TagKey<Item>> targets
) implements AffixEntry {
    public static final Codec<AttributeModifier.Operation> OPERATION_CODEC = Codec.STRING.comapFlatMap(
            value -> switch (value) {
                case "add_value" -> DataResult.success(AttributeModifier.Operation.ADD_VALUE);
                case "add_multiplied_base" -> DataResult.success(AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                case "add_multiplied_total" -> DataResult.success(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                default -> DataResult.error(() -> "Unknown attributeId operation: " + value);
            },
            AttributeModifier.Operation::getSerializedName
    );

    public static final MapCodec<AttributeModifierEntry> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(AttributeModifierEntry::attribute),
                    OPERATION_CODEC.fieldOf("operation").forGetter(AttributeModifierEntry::operation),
                    AmountRange.CODEC.fieldOf("amount").forGetter(AttributeModifierEntry::amount),
                    EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(AttributeModifierEntry::slot),
                    ResourceLocation.CODEC.listOf().fieldOf("targets").xmap(
                            ids -> ids.stream()
                                    .map(id -> TagKey.create(Registries.ITEM, id))
                                    .toList(),
                            tags -> tags.stream().map(TagKey::location).toList()
                    ).forGetter(AttributeModifierEntry::targets)
            ).apply(instance, AttributeModifierEntry::new)
    );

    public AttributeModifierEntry {
        targets = List.copyOf(targets);
    }

    public static Builder builder(ResourceLocation attribute) {
        return new Builder(attribute);
    }

    @Override
    public String type() {
        return "attribute";
    }

    @Override
    public boolean matches(Item item) {
        return targets.stream().anyMatch(tag -> item.builtInRegistryHolder().is(tag));
    }

    public static final class Builder {
        private final ResourceLocation attribute;
        private AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_VALUE;
        private AmountRange amount = AmountRange.ZERO;
        private EquipmentSlotGroup slot = EquipmentSlotGroup.ANY;
        private List<TagKey<Item>> targets = new ArrayList<>();

        private Builder(ResourceLocation attribute) {
            this.attribute = Objects.requireNonNull(attribute, "attribute");
        }

        public Builder operation(AttributeModifier.Operation operation) {
            this.operation = Objects.requireNonNull(operation, "operation");
            return this;
        }

        public Builder withAmount(float min, float max) {
            this.amount = new AmountRange(min, max);
            return this;
        }

        public Builder requiredSlot(EquipmentSlotGroup slot) {
            this.slot = Objects.requireNonNull(slot, "slot");
            return this;
        }

        public Builder withTargetTags(List<TagKey<Item>> targets) {
            this.targets = Objects.requireNonNull(targets, "targets");
            return this;
        }

        public AttributeModifierEntry build() {
            return new AttributeModifierEntry(this.attribute, this.operation, this.amount, this.slot, this.targets);
        }
    }
}
