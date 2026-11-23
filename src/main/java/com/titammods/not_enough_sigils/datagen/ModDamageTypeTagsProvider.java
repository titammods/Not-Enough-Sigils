package com.titammods.not_enough_sigils.datagen;

import com.titammods.not_enough_sigils.setup.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends TagsProvider<DamageType> {

    public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        keys(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageTypes.ARCANE);
        keys(DamageTypeTags.BYPASSES_EFFECTS).add(ModDamageTypes.ARCANE);
        keys(DamageTypeTags.BYPASSES_RESISTANCE).add(ModDamageTypes.ARCANE);
        keys(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(ModDamageTypes.ARCANE);
        keys(DamageTypeTags.BYPASSES_SHIELD).add(ModDamageTypes.ARCANE);
        keys(DamageTypeTags.BYPASSES_COOLDOWN).add(ModDamageTypes.ARCANE);
    }

    private TagAppender<ResourceKey<DamageType>, DamageType> keys(TagKey<DamageType> tag) {
        return TagAppender.forBuilder(getOrCreateRawBuilder(tag));
    }
}
