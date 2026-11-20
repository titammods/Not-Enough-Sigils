package com.titammods.animus_extended;

import com.titammods.animus_extended.client.ClientSetup;
import com.titammods.animus_extended.datagen.DataGenerators;
import com.titammods.animus_extended.network.ModNetworking;
import com.titammods.animus_extended.setup.ModCreativeTabs;
import com.titammods.animus_extended.setup.ModEntities;
import com.titammods.animus_extended.setup.ModItems;
import com.titammods.animus_extended.setup.ModMenus;
import com.titammods.animus_extended.setup.ModSigilEffects;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(AnimusExtended.MODID)
public class AnimusExtended {
    public static final String MODID = "animus_extended";

    public AnimusExtended(IEventBus modEventBus) {
        ModSigilEffects.SIGIL_EFFECTS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);

        modEventBus.addListener(ModNetworking::register);
        modEventBus.addListener(DataGenerators::gather);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientSetup.register(modEventBus);
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
