package com.titammods.not_enough_sigils.event;

import com.titammods.not_enough_sigils.sigil.effect.BerserkSigilEffect;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
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

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class BerserkEvents {
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
        BerserkSigilEffect config = SigilTracker.getEffect(player, SigilTracker.Kind.BERSERK, BerserkSigilEffect.class);
        if (config == null) {
            return;
        }

        long now = player.level().getGameTime();
        State state = STATES.computeIfAbsent(player.getUUID(), id -> new State());

        if (state.rageUntil > now) {
            state.rageUntil = now + config.rageDuration();
            applyRage(player, config);
            return;
        }

        if (state.comboStart < 0L || now - state.comboStart > config.comboWindow()) {
            state.comboStart = now;
            state.hits = 0;
        }

        state.hits++;
        if (state.hits < config.hitsRequired()) {
            if (config.speedAmplifier() >= 0 && config.comboSpeedDuration() > 0) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, config.comboSpeedDuration(), config.speedAmplifier(), false, true, true));
            }
            return;
        }

        state.comboStart = -1L;
        state.hits = 0;
        state.rageUntil = now + config.rageDuration();
        applyRage(player, config);
        player.displayClientMessage(Component.translatable("message.not_enough_sigils.berserk.rage"), true);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 0.6F, 1.3F);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        STATES.remove(event.getEntity().getUUID());
    }

    private static void applyRage(ServerPlayer player, BerserkSigilEffect config) {
        if (config.strengthAmplifier() >= 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, config.rageDuration(), config.strengthAmplifier(), false, true, true));
        }
        if (config.speedAmplifier() >= 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, config.rageDuration(), config.speedAmplifier(), false, true, true));
        }
    }
}
