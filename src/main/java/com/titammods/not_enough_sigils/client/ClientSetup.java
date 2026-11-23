package com.titammods.not_enough_sigils.client;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.OrbFluidHandler;
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
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientSetup {
    private static final int ORB_FILL_COLOR = 0x80FF0000;

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientSetup::registerRenderers);
        modEventBus.addListener(ClientSetup::registerScreens);
        modEventBus.addListener(ClientSetup::clientSetup);
        modEventBus.addListener(ClientSetup::registerItemColors);
        NeoForge.EVENT_BUS.addListener(VeinSeekerClient::onRenderLevel);
        NeoForge.EVENT_BUS.addListener(VeinSeekerClient::onLoggingOut);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MAGIC_MISSILE.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.NINJA_KNIFE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.SAERETH.get(), SaerethRenderer::new);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.GREATER_HOLDING.get(), GreaterHoldingScreen::new);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerSpiritusType(ModItems.SPIRITUS_GEM_EXALTED.get());
            registerSpiritusType(ModItems.SPIRITUS_GEM_PRIMORDIAL.get());
            for (Item orb : ModItems.bloodOrbs()) {
                registerOrbFill(orb);
            }
        });
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, layer) -> layer == 1 ? ORB_FILL_COLOR : 0xFFFFFFFF, ModItems.bloodOrbs());
    }

    private static void registerOrbFill(Item orb) {
        ItemProperties.register(orb, NeoVitae.rl("fill_level"), (stack, level, entity, seed) -> {
            int capacity = OrbFluidHandler.getOrbFluidCapacity(stack);
            if (capacity <= 0) {
                return 0.0F;
            }
            SimpleFluidContent fluid = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
            return fluid.isEmpty() ? 0.0F : (float) fluid.getAmount() / capacity;
        });
    }

    private static void registerSpiritusType(Item item) {
        ItemProperties.register(item, NeoVitae.TYPE_PROPERTY,
                (stack, level, entity, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW).ordinal());
    }
}
