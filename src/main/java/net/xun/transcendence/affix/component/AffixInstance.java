package net.xun.transcendence.affix.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.xun.transcendence.affix.AffixType;

public record AffixInstance(
        ResourceLocation id,
        AffixType type,
        ResourceLocation attribute,
        AttributeModifier.Operation operation,
        double amount,
        EquipmentSlotGroup slot,
        ResourceLocation modifierId
) {
    public static final Codec<AffixInstance> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(AffixInstance::id),
                    AffixType.CODEC.fieldOf("type").forGetter(AffixInstance::type),
                    ResourceLocation.CODEC.fieldOf("attributeId").forGetter(AffixInstance::attribute),
                    AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AffixInstance::operation),
                    Codec.DOUBLE.fieldOf("amount").forGetter(AffixInstance::amount),
                    EquipmentSlotGroup.CODEC.fieldOf("slot").forGetter(AffixInstance::slot),
                    ResourceLocation.CODEC.fieldOf("modifier_id").forGetter(AffixInstance::modifierId)
            ).apply(instance, AffixInstance::new)
    );
}
