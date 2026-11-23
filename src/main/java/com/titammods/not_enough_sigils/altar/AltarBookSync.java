package com.titammods.not_enough_sigils.altar;

import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.structure.AltarMultiblockBuilder;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import com.mojang.logging.LogUtils;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AltarBookSync {
    public static final ResourceLocation ASCENDANT_MULTIBLOCK = NotEnoughSigils.rl("altar_seven");
    public static final ResourceLocation DIVINE_MULTIBLOCK = NotEnoughSigils.rl("altar_eight");

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<ResourceLocation> INJECTED_IDS = new HashSet<>();

    public static void register(IEventBus bus) {
        bus.addListener(AltarBookSync::onServerStarted);
        bus.addListener(EventPriority.HIGHEST, AltarBookSync::onDatapackSync);
    }

    private static void onServerStarted(ServerStartedEvent event) {
        install(event.getServer());
    }

    private static void onDatapackSync(OnDatapackSyncEvent event) {
        install(event.getPlayerList().getServer());
    }

    private static void install(MinecraftServer server) {
        if (server == null || !ModList.get().isLoaded("modonomicon")) {
            return;
        }
        Map<ResourceLocation, Multiblock> multiblocks = MultiblockDataManager.get().getMultiblocks();
        for (ResourceLocation id : INJECTED_IDS) {
            multiblocks.remove(id);
        }
        INJECTED_IDS.clear();

        RegistryAccess registries = server.registryAccess();
        Optional<Registry<AltarTier>> registry = registries.registry(NVRegistries.Keys.ALTAR_TIER_KEY);
        if (registry.isEmpty()) {
            return;
        }
        inject(multiblocks, registry.get(), registries, ModAltarTiers.ASCENDANT, ASCENDANT_MULTIBLOCK);
        inject(multiblocks, registry.get(), registries, ModAltarTiers.DIVINE, DIVINE_MULTIBLOCK);
    }

    private static void inject(Map<ResourceLocation, Multiblock> multiblocks, Registry<AltarTier> registry, RegistryAccess registries,
                               ResourceKey<AltarTier> key, ResourceLocation id) {
        if (multiblocks.containsKey(id)) {
            return;
        }
        Optional<Holder.Reference<AltarTier>> holder = registry.getHolder(key);
        if (holder.isEmpty()) {
            return;
        }
        try {
            Multiblock multiblock = AltarMultiblockBuilder.build(holder.get().value(), registries);
            multiblock.setId(id);
            multiblocks.put(id, multiblock);
            INJECTED_IDS.add(id);
        } catch (Throwable throwable) {
            LOGGER.error("Failed to build altar multiblock {}", id, throwable);
        }
    }
}
