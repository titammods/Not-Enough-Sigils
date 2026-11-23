package com.titammods.not_enough_sigils.client;

import com.titammods.not_enough_sigils.setup.ModEntities;
import com.titammods.not_enough_sigils.setup.ModMenus;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientSetup {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientSetup::registerRenderers);
        modEventBus.addListener(ClientSetup::registerScreens);
        modEventBus.addListener(VeinSeekerClient::registerPipelines);
        NeoForge.EVENT_BUS.addListener(VeinSeekerClient::onRenderLevel);
        NeoForge.EVENT_BUS.addListener(VeinSeekerClient::onLoggingOut);
        NeoForge.EVENT_BUS.addListener(SaerethSigilMarkers::onClientTick);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MAGIC_MISSILE.get(), MagicMissileRenderer::new);
        event.registerEntityRenderer(ModEntities.NINJA_KNIFE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.SAERETH.get(), SaerethRenderer::new);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.GREATER_HOLDING.get(), GreaterHoldingScreen::new);
    }
}
