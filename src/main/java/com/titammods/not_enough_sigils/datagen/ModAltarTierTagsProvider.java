package com.titammods.not_enough_sigils.datagen;

import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.altar.ModAltarTiers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModAltarTierTagsProvider extends TagsProvider<AltarTier> {

    public ModAltarTierTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, NVRegistries.Keys.ALTAR_TIER_KEY, lookupProvider, NotEnoughSigils.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(NVTags.Tiers.VALID_TIERS).add(ModAltarTiers.ASCENDANT).add(ModAltarTiers.DIVINE);
    }
}
