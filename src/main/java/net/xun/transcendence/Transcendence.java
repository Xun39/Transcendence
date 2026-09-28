package net.xun.transcendence;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.xun.lib.common.api.ModSetup;
import net.xun.transcendence.registry.TDataComponents;
import net.xun.transcendence.registry.TItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(Transcendence.MOD_ID)
public class Transcendence {
    public static final String MOD_ID = "transcendence";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Transcendence(IEventBus modEventBus, ModContainer modContainer) {
        ModSetup.setModId(MOD_ID);
        NeoForge.EVENT_BUS.register(this);

        TItems.ITEMS.register(modEventBus);
        TDataComponents.DATA_COMPONENTS.register(modEventBus);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
