package com.titammods.animus_extended.datagen;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.MonkSigilEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentTagsProvider extends TagsProvider<Enchantment> {

    public ModEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, Registries.ENCHANTMENT, lookupProvider, AnimusExtended.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MonkSigilEnchantments.BLACKLIST).add(Enchantments.SHARPNESS);
    }
}
