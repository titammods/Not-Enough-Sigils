package com.titammods.not_enough_sigils.datagen;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, NotEnoughSigils.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Blocks.PONYMEAL_EXCLUDED).add(Blocks.WITHER_ROSE);
    }
}
