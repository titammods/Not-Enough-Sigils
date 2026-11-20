package com.titammods.not_enough_sigils.client;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.titammods.not_enough_sigils.setup.ModEntities;
import com.titammods.not_enough_sigils.setup.ModItems;
import com.titammods.not_enough_sigils.setup.ModMenus;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientSetup {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientSetup::registerRenderers);
        modEventBus.addListener(ClientSetup::registerScreens);
        modEventBus.addListener(ClientSetup::clientSetup);
        NeoForge.EVENT_BUS.addListener(VeinSeekerClient::onRenderLevel);
        NeoForge.EVENT_BUS.addListener(VeinSeekerClient::onLoggingOut);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MAGIC_MISSILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.NINJA_KNIFE.get(), ThrownItemRenderer::new);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.GREATER_HOLDING.get(), GreaterHoldingScreen::new);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerSpiritusType(ModItems.SPIRITUS_GEM_EXALTED.get());
            registerSpiritusType(ModItems.SPIRITUS_GEM_PRIMORDIAL.get());
        });
    }

    private static void registerSpiritusType(Item item) {
        ItemProperties.register(item, NeoVitae.TYPE_PROPERTY,
                (stack, level, entity, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW).ordinal());
    }
}
