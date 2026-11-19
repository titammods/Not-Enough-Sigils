package com.titammods.animus_extended.client;

import com.titammods.animus_extended.setup.ModEntities;
import com.titammods.animus_extended.setup.ModMenus;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientSetup {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientSetup::registerRenderers);
        modEventBus.addListener(ClientSetup::registerScreens);
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
}
