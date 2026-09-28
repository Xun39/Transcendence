package net.xun.transcendence.affix;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.xun.lib.common.api.util.CommonUtils;
import net.xun.transcendence.affix.component.AffixData;
import net.xun.transcendence.affix.component.AffixInstance;
import net.xun.transcendence.registry.TDataComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.ToIntFunction;

public final class AffixHelper {
    public record Candidate(ResourceLocation id, Affix affix, AffixEntry chosenEntry) {
    }

    public static boolean rollAffix(ItemStack stack, AffixType type, RandomSource random) {
        if (stack.isEmpty() || stack.getCount() != 1 || hasType(stack, type)) {
            return false;
        }

        List<Candidate> candidates = findCandidates(stack, type);
        Candidate chosen = weightedRandom(candidates, entry -> entry.affix().weight(), random);
        if (chosen == null) {
            return false;
        }

        AffixEntry entry = chosen.chosenEntry();
        AffixInstance rolled = null;
        switch (chosen.chosenEntry().type()) {
            case "attribute" -> {
                AttributeModifierEntry modifierEntry = ((AttributeModifierEntry) entry);
                double amount = modifierEntry.amount().roll(random);
                ResourceLocation modifierId = CommonUtils.modLoc("affix/" + UUID.randomUUID());
                rolled = new AffixInstance(chosen.id(), chosen.affix().type(), modifierEntry.attribute(), modifierEntry.operation(), amount, modifierEntry.slot(), modifierId);
            }
        }

        AffixDataBuilder builder = AffixDataBuilder.from(stack);
        builder.affixes.add(rolled);

        boolean generatedName = builder.generatedName;
        if (!generatedName && !stack.has(DataComponents.CUSTOM_NAME)) {
            generatedName = true;
        }
        builder.generatedName = generatedName;

        stack.set(TDataComponents.AFFIXES.get(), new AffixData(builder.affixes, builder.generatedName));

        if (generatedName) {
            updateGeneratedName(stack);
        }

        return true;
    }

    public static void clearAffixes(ItemStack stack) {
        AffixData data = stack.get(TDataComponents.AFFIXES.get());

        if (data == null) {
            return;
        }

        stack.remove(TDataComponents.AFFIXES.get());

        if (data.generatedName()) {
            stack.remove(DataComponents.CUSTOM_NAME);
        }
    }

    private static List<Candidate> findCandidates(ItemStack stack, AffixType type) {
        List<Candidate> result = new ArrayList<>();

        for (var entry : AffixManager.INSTANCE.entries()) {
            ResourceLocation id = entry.getKey();
            Affix affix = entry.getValue();

            if (affix.type() != type || affix.operation() != AffixOperation.MODIFY_ATTRIBUTE) {
                continue;
            }

            affix.entries().forEach(entry1 -> {
                if (entry1.matches(stack.getItem()) && affix.weight() > 0) {
                    result.add(new Candidate(id, affix, entry1));
                }
            });
        }

        return result;
    }

    private static Candidate weightedRandom(List<Candidate> entries, ToIntFunction<Candidate> weightFunction, RandomSource random) {
        long total = 0;
        for (Candidate entry : entries) {
            total += Math.max(0, weightFunction.applyAsInt(entry));
        }

        if (total <= 0) {
            return null;
        }

        long roll = nextLong(random, total);

        for (Candidate entry : entries) {
            int weight = Math.max(0, weightFunction.applyAsInt(entry));
            if (roll < weight) {
                return entry;
            }
            roll -= weight;
        }

        return entries.getLast();
    }

    private static long nextLong(RandomSource random, long bound) {
        if (bound <= 0) throw new IllegalArgumentException("bound must be positive");
        long r = random.nextLong();
        long m = bound - 1;

        if ((bound & m) == 0L) return r & m;
        long u = r >>> 1;
        while (u + m - (r = u % bound) < 0L) {
            u = random.nextLong() >>> 1;
        }
        return r;
    }

    public static boolean hasType(ItemStack stack, AffixType type) {
        AffixData data = stack.get(TDataComponents.AFFIXES.get());
        return data != null && data.affixes().stream().anyMatch(affix -> affix.type() == type);
    }

    public static String translationKey(AffixInstance affix) {
        String path = affix.id().getPath().replace('/', '.');
        return CommonUtils.translationKey("affix", affix.id().getNamespace(), path);
    }

    public static Component formatValue(Attribute attribute, AffixInstance affix) {
        Component attributeName = Component.translatable(attribute.getDescriptionId());

        String amount;
        if (affix.operation() == AttributeModifier.Operation.ADD_VALUE) {
            amount = String.format(Locale.ROOT, "%+.2f", affix.amount());
        }
        else {
            amount = String.format(Locale.ROOT, "%+.2f%%", affix.amount() * 100.0);
        }

        return Component.literal(amount + " " + attributeName.getString()).withStyle(affix.amount() >= 0 ? ChatFormatting.BLUE : ChatFormatting.RED);
    }

    private static void updateGeneratedName(ItemStack stack) {
        AffixData data = stack.get(TDataComponents.AFFIXES.get());
        if (data == null || !data.generatedName()) {
            return;
        }

        Component name = Component.translatable(stack.getItem().getDescriptionId());

        for (AffixInstance affix : data.affixes()) {
            if (affix.type() == AffixType.PREFIX) {
                name = Component.translatable("affix.transcendence.name.prefix", Component.translatable(translationKey(affix)), name);
            }
        }

        for (AffixInstance affix : data.affixes()) {
            if (affix.type() == AffixType.SUFFIX) {
                name = Component.translatable("affix.transcendence.name.suffix", name, Component.translatable(translationKey(affix)));
            }
        }

        stack.set(DataComponents.CUSTOM_NAME, name);
    }

    private static final class AffixDataBuilder {
        private final List<AffixInstance> affixes;
        private boolean generatedName;

        private AffixDataBuilder(List<AffixInstance> affixes, boolean generatedName) {
            this.affixes = affixes;
            this.generatedName = generatedName;
        }

        private static AffixDataBuilder from(ItemStack stack) {
            AffixData data = stack.get(TDataComponents.AFFIXES.get());

            if (data == null) {
                return new AffixDataBuilder(new ArrayList<>(), false);
            }

            return new AffixDataBuilder(new ArrayList<>(data.affixes()), data.generatedName());
        }
    }
}
