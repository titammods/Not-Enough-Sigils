package com.titammods.not_enough_sigils.event;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import com.titammods.not_enough_sigils.sigil.SkywardSoulState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class SkywardSoulEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        if (data.getBoolean(SkywardSoulState.FLIGHT_TAG) && !SigilTracker.isActive(player, SigilTracker.Kind.SKYWARD_SOUL)) {
            SkywardSoulState.revokeFlight(player);
        }
        if (data.getBoolean(SkywardSoulState.FALL_SAFE_TAG) && (player.onGround() || player.isInWater())) {
            data.remove(SkywardSoulState.FALL_SAFE_TAG);
        }
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        if (data.getBoolean(SkywardSoulState.FALL_SAFE_TAG) || SigilTracker.isActive(player, SigilTracker.Kind.SKYWARD_SOUL)) {
            data.remove(SkywardSoulState.FALL_SAFE_TAG);
            event.setCanceled(true);
        }
    }
}
