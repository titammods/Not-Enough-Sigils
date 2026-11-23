package com.titammods.not_enough_sigils.datagen;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NotEnoughSigils.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Blocks.PONYMEAL_EXCLUDED).add(Blocks.WITHER_ROSE);
        tag(ModTags.Blocks.T7_CAPSTONES_RUINA).add(neovitae("spiritus_ruina_crystal"));
        tag(ModTags.Blocks.T7_CAPSTONES_NIHILUM).add(neovitae("spiritus_nihilum_crystal"));
        tag(ModTags.Blocks.T7_CAPSTONES_INVICTUS).add(neovitae("spiritus_invictus_crystal"));
        tag(ModTags.Blocks.T7_CAPSTONES_VINDICTA).add(neovitae("spiritus_vindicta_crystal"));
        tag(ModTags.Blocks.T8_CAPSTONES).add(neovitae("sanguine_deus_block"));
        tag(ModTags.Blocks.T8_CORNERSTONES).add(neovitae("prismatic_demonite"));
    }

    private static ResourceKey<Block> neovitae(String path) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("neovitae", path));
    }
}
