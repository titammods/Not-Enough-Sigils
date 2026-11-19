package com.titammods.animus_extended.event;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.DelayedDamage;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class HellbrandEvents {
    private static final String STACKS_TAG = "animus_extended_hellbrand_stacks";
    private static final String TICK_TAG = "animus_extended_hellbrand_tick";
    private static final int MAX_STACKS = 5;
    private static final long STACK_DURATION = 200L;
    private static final float BASE_BURST = 5.0F;
    private static final float BURST_HEALTH_RATIO = 0.15F;
    private static final float MAX_BURST = 50.0F;

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getSource().getDirectEntity() != player) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target == player || event.getNewDamage() <= 0.0F || !target.isAlive()) {
            return;
        }
        if (!SigilTracker.isActive(player, SigilTracker.Kind.HELLBRAND)) {
            return;
        }

        ServerLevel level = player.serverLevel();
        long now = level.getGameTime();
        CompoundTag data = target.getPersistentData();
        int stacks = now - data.getLong(TICK_TAG) > STACK_DURATION ? 0 : data.getInt(STACKS_TAG);
        stacks++;
        data.putLong(TICK_TAG, now);

        if (stacks >= MAX_STACKS) {
            data.remove(STACKS_TAG);
            float damage = Math.min(MAX_BURST, BASE_BURST + target.getMaxHealth() * BURST_HEALTH_RATIO);
            DelayedDamage.arcane(target, player, damage, HellbrandEvents::burstEffects);
        } else {
            data.putInt(STACKS_TAG, stacks);
        }

        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, target.getX(), target.getY() + target.getBbHeight() * 0.6D, target.getZ(),
                stacks * 3, 0.3D, 0.3D, 0.3D, 0.02D);
    }

    private static void burstEffects(LivingEntity target) {
        if (!(target.level() instanceof ServerLevel level)) {
            return;
        }
        level.sendParticles(ParticleTypes.SOUL, target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                20, 0.4D, 0.4D, 0.4D, 0.05D);
        level.sendParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.2F, 0.7F);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        DelayedDamage.flush();
    }
}
