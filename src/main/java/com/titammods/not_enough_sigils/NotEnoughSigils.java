package com.titammods.not_enough_sigils;

import com.titammods.not_enough_sigils.client.ClientSetup;
import com.titammods.not_enough_sigils.datagen.DataGenerators;
import com.titammods.not_enough_sigils.network.ModNetworking;
import com.titammods.not_enough_sigils.saereth.SaerethSettings;
import com.titammods.not_enough_sigils.setup.ModCreativeTabs;
import com.titammods.not_enough_sigils.setup.ModEntities;
import com.titammods.not_enough_sigils.setup.ModItems;
import com.titammods.not_enough_sigils.setup.ModMenus;
import com.titammods.not_enough_sigils.setup.ModRituals;
import com.titammods.not_enough_sigils.setup.ModSigilEffects;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(NotEnoughSigils.MODID)
public class NotEnoughSigils {
    public static final String MODID = "not_enough_sigils";

    public NotEnoughSigils(IEventBus modEventBus) {
        ModSigilEffects.SIGIL_EFFECTS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModRituals.RITUALS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);

        modEventBus.addListener(ModNetworking::register);
        DataGenerators.register(modEventBus);
        modEventBus.addListener(ModEntities::registerAttributes);
        modEventBus.addListener(ModItems::modifyComponents);
        NeoForge.EVENT_BUS.addListener(SaerethSettings::onAddReloadListeners);

        if (FMLEnvironment.getDist().isClient()) {
            ClientSetup.register(modEventBus);
        }
    }

    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}