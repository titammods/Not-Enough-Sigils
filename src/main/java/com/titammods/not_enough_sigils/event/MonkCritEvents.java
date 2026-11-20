package com.titammods.not_enough_sigils.event;

import com.breakinblocks.animusnv.events.MonkSigilEventHandler;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class MonkCritEvents {
    private static final float CRIT_MULTIPLIER = 2.0F;

    private static final Map<UUID, PendingCrit> PENDING = new ConcurrentHashMap<>();

    private record PendingCrit(int targetId, long tick) {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onCriticalHit(CriticalHitEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!event.isCriticalHit() || !player.getMainHandItem().isEmpty()) {
            return;
        }
        if (!MonkSigilEventHandler.hasActiveMonkSigil(player)) {
            return;
        }
        event.setDamageMultiplier(1.0F);
        PENDING.put(player.getUUID(), new PendingCrit(event.getTarget().getId(), player.level().getGameTime()));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getSource().getDirectEntity() != player) {
            return;
        }
        PendingCrit crit = PENDING.remove(player.getUUID());
        if (crit == null) {
            return;
        }
        if (crit.targetId() != event.getEntity().getId() || crit.tick() != player.level().getGameTime()) {
            return;
        }
        event.setAmount(event.getAmount() * CRIT_MULTIPLIER);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PENDING.remove(event.getEntity().getUUID());
    }
}
