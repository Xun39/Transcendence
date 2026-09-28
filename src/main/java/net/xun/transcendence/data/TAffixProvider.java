package net.xun.transcendence.data;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import net.xun.lib.common.api.util.CommonUtils;
import net.xun.transcendence.Transcendence;
import net.xun.transcendence.affix.Affix;
import net.xun.transcendence.affix.AffixOperation;
import net.xun.transcendence.affix.AttributeModifierEntry;
import net.xun.transcendence.util.TTags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TAffixProvider extends JsonCodecProvider<Affix> {
    public TAffixProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, PackOutput.Target.DATA_PACK, "affixes", PackType.SERVER_DATA, Affix.CODEC, lookupProvider, Transcendence.MOD_ID, existingFileHelper);
    }

    @Override
    protected void gather() {
        unconditional(id("prefix", "sharp"),
                Affix.Builder.prefix(AffixOperation.MODIFY_ATTRIBUTE, 100)
                        .entry(AttributeModifierEntry.builder(getAttributeKey(Attributes.ATTACK_DAMAGE))
                                .operation(AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                                .withAmount(0.1F, 0.2F)
                                .requiredSlot(EquipmentSlotGroup.MAINHAND)
                                .withTargetTags(List.of(Tags.Items.MELEE_WEAPON_TOOLS))
                                .build()
                        )
                        .entry(AttributeModifierEntry.builder(getAttributeKey(Attributes.ATTACK_DAMAGE))
                                .operation(AttributeModifier.Operation.ADD_VALUE)
                                .withAmount(1.0F, 2.0F)
                                .requiredSlot(EquipmentSlotGroup.ANY)
                                .withTargetTags(List.of(TTags.Items.CHARM.tag))
                                .build()
                        )
                        .build()
        );
        unconditional(id("suffix", "quality"),
                Affix.Builder.suffix(AffixOperation.MODIFY_ATTRIBUTE, 100)
                        .entry(AttributeModifierEntry.builder(getAttributeKey(Attributes.ATTACK_DAMAGE))
                                .operation(AttributeModifier.Operation.ADD_VALUE)
                                .withAmount(1.0F, 2.0F)
                                .requiredSlot(EquipmentSlotGroup.MAINHAND)
                                .withTargetTags(List.of(Tags.Items.MELEE_WEAPON_TOOLS))
                                .build()
                        )
                        .entry(AttributeModifierEntry.builder(getAttributeKey(Attributes.ATTACK_DAMAGE))
                                .operation(AttributeModifier.Operation.ADD_VALUE)
                                .withAmount(2.0F, 3.0F)
                                .requiredSlot(EquipmentSlotGroup.MAINHAND)
                                .withTargetTags(List.of(TTags.Items.CHARM.tag))
                                .build()
                        )
                        .build()
        );

        unconditional(id("prefix", "fast"),
                Affix.Builder.prefix(AffixOperation.MODIFY_ATTRIBUTE, 100)
                        .entry(AttributeModifierEntry.builder(getAttributeKey(Attributes.ATTACK_SPEED))
                                .operation(AttributeModifier.Operation.ADD_VALUE)
                                .withAmount(0.5F, 1.0F)
                                .requiredSlot(EquipmentSlotGroup.MAINHAND)
                                .withTargetTags(List.of(Tags.Items.MELEE_WEAPON_TOOLS))
                                .build()
                        )
                        .build()
        );
        unconditional(id("suffix", "well_forged"),
                Affix.Builder.prefix(AffixOperation.MODIFY_ATTRIBUTE, 100)
                        .entry(AttributeModifierEntry.builder(getAttributeKey(Attributes.ATTACK_SPEED))
                                .operation(AttributeModifier.Operation.ADD_VALUE)
                                .withAmount(0.3F, 0.5F)
                                .requiredSlot(EquipmentSlotGroup.MAINHAND)
                                .withTargetTags(List.of(Tags.Items.MELEE_WEAPON_TOOLS))
                                .build()
                        )
                        .build()
        );
    }

    private ResourceLocation getAttributeKey(Holder<Attribute> attributeHolder) {
        return BuiltInRegistries.ATTRIBUTE.getKey(attributeHolder.value());
    }

    private static ResourceLocation id(String type, String name) {
        return CommonUtils.modLoc(type + "/" + name);
    }
}
