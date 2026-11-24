package com.titammods.not_enough_sigils.datagen;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends TagsProvider<DamageType> {

    public ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, NotEnoughSigils.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageTypes.ARCANE);
        tag(DamageTypeTags.BYPASSES_EFFECTS).add(ModDamageTypes.ARCANE);
        tag(DamageTypeTags.BYPASSES_RESISTANCE).add(ModDamageTypes.ARCANE);
        tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(ModDamageTypes.ARCANE);
        tag(DamageTypeTags.BYPASSES_SHIELD).add(ModDamageTypes.ARCANE);
        tag(DamageTypeTags.BYPASSES_COOLDOWN).add(ModDamageTypes.ARCANE);
        tag(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageTypes.EXECUTION);
        tag(DamageTypeTags.BYPASSES_EFFECTS).add(ModDamageTypes.EXECUTION);
        tag(DamageTypeTags.BYPASSES_RESISTANCE).add(ModDamageTypes.EXECUTION);
        tag(DamageTypeTags.BYPASSES_ENCHANTMENTS).add(ModDamageTypes.EXECUTION);
        tag(DamageTypeTags.BYPASSES_SHIELD).add(ModDamageTypes.EXECUTION);
        tag(DamageTypeTags.BYPASSES_COOLDOWN).add(ModDamageTypes.EXECUTION);
        tag(DamageTypeTags.NO_KNOCKBACK).add(ModDamageTypes.EXECUTION);
    }
}
