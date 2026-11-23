package com.titammods.not_enough_sigils.datagen;

import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DataGenerators::onClientData);
        modEventBus.addListener(DataGenerators::onServerData);
    }

    private static void onClientData(GatherDataEvent.Client event) {
        event.createProvider(ModItemModelProvider::new);
    }

    private static void onServerData(GatherDataEvent.Server event) {
        event.createDatapackRegistryObjects(new RegistrySetBuilder()
                .add(Registries.DAMAGE_TYPE, ModRegistryContent::damageTypes)
                .add(SigilTypeRegistry.SIGIL_TYPE_KEY, ModRegistryContent::sigilTypes));

        event.createBlockAndItemTags(ModBlockTagsProvider::new, ModItemTagsProvider::new);
        event.createProvider(ModEnchantmentTagsProvider::new);
        event.createProvider(ModDamageTypeTagsProvider::new);
        event.createProvider(ModDataMapProvider::new);
        event.createProvider(ModRecipeProvider.Runner::new);
        event.createProvider(SaerethSettingsProvider::new);
    }
}