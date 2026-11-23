package com.titammods.not_enough_sigils.datagen;

import com.titammods.not_enough_sigils.sigil.MonkSigilEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentTagsProvider extends TagsProvider<Enchantment> {

    public ModEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ENCHANTMENT, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        keys(MonkSigilEnchantments.BLACKLIST).add(Enchantments.SHARPNESS);
    }

    private TagAppender<ResourceKey<Enchantment>, Enchantment> keys(TagKey<Enchantment> tag) {
        return TagAppender.forBuilder(getOrCreateRawBuilder(tag));
    }
}
