package net.xun.transcendence.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.xun.transcendence.Transcendence;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Transcendence.MOD_ID)
public class TranscendenceData {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();
        boolean client = event.includeClient();
        boolean server = event.includeServer();

        BlockTagsProvider blockTagsProvider = new TBlockTags(output, registries, helper);

        generator.addProvider(server, blockTagsProvider);
        generator.addProvider(server, new TItemTags(output, registries, blockTagsProvider.contentsGetter(), helper));

        generator.addProvider(server, new TAffixProvider(output, registries, helper));
    }
}
