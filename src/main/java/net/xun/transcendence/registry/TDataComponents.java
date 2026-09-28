package net.xun.transcendence.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.transcendence.Transcendence;
import net.xun.transcendence.affix.component.AffixData;

public class TDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Transcendence.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AffixData>> AFFIXES =
            DATA_COMPONENTS.registerComponentType("affixes", builder -> builder.persistent(AffixData.CODEC));
}
