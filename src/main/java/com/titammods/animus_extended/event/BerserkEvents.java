package com.titammods.animus_extended.event;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class BerserkEvents {
    private static final int HITS_REQUIRED = 10;
    private static final long COMBO_WINDOW = 300L;
    private static final int RAGE_TICKS = 400;
    private static final int STRENGTH_AMPLIFIER = 2;
    private static final int COMBO_SPEED_TICKS = 100;
    private static final int SPEED_AMPLIFIER = 1;

    private static final Map<UUID, State> STATES = new ConcurrentHashMap<>();

    private static class State {
        private long comboStart = -1L;
        private int hits;
        private long rageUntil = -1L;
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getSource().getDirectEntity() != player) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target == player || event.getNewDamage() <= 0.0F) {
            return;
        }
        if (!SigilTracker.isActive(player, SigilTracker.Kind.BERSERK)) {
            return;
        }

        long now = player.level().getGameTime();
        State state = STATES.computeIfAbsent(player.getUUID(), id -> new State());

        if (state.rageUntil > now) {
            state.rageUntil = now + RAGE_TICKS;
            applyRage(player);
            return;
        }

        if (state.comboStart < 0L || now - state.comboStart > COMBO_WINDOW) {
            state.comboStart = now;
            state.hits = 0;
        }

        state.hits++;
        if (state.hits < HITS_REQUIRED) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, COMBO_SPEED_TICKS, SPEED_AMPLIFIER, false, true, true));
            return;
        }

        state.comboStart = -1L;
        state.hits = 0;
        state.rageUntil = now + RAGE_TICKS;
        applyRage(player);
        player.displayClientMessage(Component.translatable("message.animus_extended.berserk.rage"), true);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 0.6F, 1.3F);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        STATES.remove(event.getEntity().getUUID());
    }

    private static void applyRage(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, RAGE_TICKS, STRENGTH_AMPLIFIER, false, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, RAGE_TICKS, SPEED_AMPLIFIER, false, true, true));
    }
}
