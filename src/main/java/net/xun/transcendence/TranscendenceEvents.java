package net.xun.transcendence;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.xun.transcendence.affix.AffixManager;
import net.xun.transcendence.affix.AffixType;
import net.xun.transcendence.affix.AffixHelper;
import net.xun.transcendence.affix.component.AffixData;
import net.xun.transcendence.affix.component.AffixInstance;
import net.xun.transcendence.registry.TDataComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = Transcendence.MOD_ID)
public class TranscendenceEvents {
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(AffixManager.INSTANCE);
    }

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        AffixData data = stack.get(TDataComponents.AFFIXES.get());

        if (data == null) {
            return;
        }

        for (AffixInstance affix : data.affixes()) {
            Optional<Holder.Reference<Attribute>> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(affix.attribute());

            if (attribute.isEmpty()) {
                Transcendence.LOGGER.warn("Affix {} references unknown attributeId {}", affix.id(), affix.attribute());
                continue;
            }

            AttributeModifier modifier = new AttributeModifier(affix.modifierId(), affix.amount(), affix.operation());

            event.addModifier(attribute.get(), modifier, affix.slot());
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        AffixData data = stack.get(TDataComponents.AFFIXES.get());

        if (data == null || data.affixes().isEmpty()) {
            return;
        }

        List<Component> lines = new ArrayList<>();

        for (AffixInstance affix : data.affixes()) {
            Optional<Holder.Reference<Attribute>> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(affix.attribute());

            if (attribute.isEmpty()) {
                continue;
            }

            String affixNameKey = AffixHelper.translationKey(affix);
            Component affixName = Component.translatable(affixNameKey);
            Component value = AffixHelper.formatValue(attribute.get().value(), affix);

            lines.add(Component.translatable("tooltip.transcendence.affix", affixName, value));
        }

        event.getToolTip().addAll(lines);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("transcendence").then(Commands.literal("affix").then(Commands.literal("roll").then(Commands.argument("kind", StringArgumentType.word()).suggests((context, builder) -> {
            builder.suggest("prefix");
            builder.suggest("suffix");
            builder.suggest("both");
            return builder.buildFuture();
        }).executes(context -> rollCommand(context.getSource(), StringArgumentType.getString(context, "kind"))))).then(Commands.literal("clear").executes(context -> clearCommand(context.getSource())))));
    }

    private static int rollCommand(CommandSourceStack source, String kind) {
        ItemStack stack = source.getEntity() instanceof net.minecraft.world.entity.player.Player player ? player.getMainHandItem() : ItemStack.EMPTY;

        if (stack.isEmpty()) {
            source.sendFailure(Component.translatable("transcendence.affix.command.no_item"));
            return 0;
        }

        RandomSource random = RandomSource.create();
        int count;

        switch (kind.toLowerCase(java.util.Locale.ROOT)) {
            case "prefix" -> count = AffixHelper.rollAffix(stack, AffixType.PREFIX, random) ? 1 : 0;
            case "suffix" -> count = AffixHelper.rollAffix(stack, AffixType.SUFFIX, random) ? 1 : 0;
            case "both" -> {
                count = 0;
                if (AffixHelper.rollAffix(stack, AffixType.PREFIX, random)) count++;
                if (AffixHelper.rollAffix(stack, AffixType.SUFFIX, random)) count++;
            }
            default -> {
                source.sendFailure(Component.translatable("transcendence.affix.command.invalid_kind"));
                return 0;
            }
        }

        if (count == 0) {
            source.sendFailure(Component.translatable("transcendence.affix.command.no_match"));
            return 0;
        }

        int finalCount = count;
        source.sendSuccess(() -> Component.translatable("transcendence.affix.command.success", finalCount), false);
        return count;
    }

    private static int clearCommand(CommandSourceStack source) {
        if (!(source.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
            source.sendFailure(Component.translatable("transcendence.chosenEntry.command.players_only"));
            return 0;
        }

        ItemStack stack = player.getMainHandItem();
        AffixHelper.clearAffixes(stack);
        source.sendSuccess(() -> Component.translatable("transcendence.affix.command.cleared"), false);
        return 1;
    }
}
