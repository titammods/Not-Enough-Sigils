package com.titammods.not_enough_sigils.datagen;

import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import com.titammods.not_enough_sigils.altar.ModAltarTiers;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void gather(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        event.createDatapackRegistryObjects(new RegistrySetBuilder()
                .add(Registries.DAMAGE_TYPE, ModRegistryContent::damageTypes)
                .add(SigilTypeRegistry.SIGIL_TYPE_KEY, ModRegistryContent::sigilTypes)
                .add(NVRegistries.Keys.ALTAR_TIER_KEY, ModAltarTiers::bootstrap));

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, fileHelper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, fileHelper));
        generator.addProvider(event.includeServer(), ModBlockLootProvider.create(output, event.getLookupProvider()));

        ModBlockTagsProvider blockTags = generator.addProvider(event.includeServer(),
                new ModBlockTagsProvider(output, event.getLookupProvider(), fileHelper));
        generator.addProvider(event.includeServer(),
                new ModItemTagsProvider(output, event.getLookupProvider(), blockTags.contentsGetter(), fileHelper));
        generator.addProvider(event.includeServer(), new ModEnchantmentTagsProvider(output, event.getLookupProvider(), fileHelper));
        generator.addProvider(event.includeServer(), new ModDamageTypeTagsProvider(output, event.getLookupProvider(), fileHelper));
        generator.addProvider(event.includeServer(), new ModDataMapProvider(output, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new SaerethSettingsProvider(output));
        generator.addProvider(event.includeServer(), new ModAltarTierTagsProvider(output, event.getLookupProvider(), fileHelper));
        generator.addProvider(event.includeServer(), new ModBookProvider(output));
    }
}
