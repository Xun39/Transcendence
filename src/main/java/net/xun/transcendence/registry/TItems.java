package net.xun.transcendence.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.transcendence.Transcendence;

public class TItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Transcendence.MOD_ID);

    public static final DeferredHolder<Item, Item> TEST_CHARM = ITEMS.registerItem("test_charm", Item::new, new Item.Properties().stacksTo(1));
}
