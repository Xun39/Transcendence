package net.xun.transcendence.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.xun.transcendence.Transcendence;
import net.xun.transcendence.registry.TItems;
import net.xun.transcendence.util.TTags;

import java.util.concurrent.CompletableFuture;

public class TItemTags extends ItemTagsProvider {
    public TItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Transcendence.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TTags.Items.CHARM.tag).add(TItems.TEST_CHARM.get());
    }
}
