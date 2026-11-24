package com.titammods.not_enough_sigils.event;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModDamageTypes;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import com.titammods.not_enough_sigils.sigil.effect.ExecutionerSigilEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class ExecutionerEvents {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDamage(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (source.is(ModDamageTypes.EXECUTION) || !(source.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target == player || target instanceof Player || !target.isAlive() || !(target.level() instanceof ServerLevel level)) {
            return;
        }
        ExecutionerSigilEffect config = SigilTracker.getEffect(player, SigilTracker.Kind.EXECUTIONER, ExecutionerSigilEffect.class);
        if (config == null || (config.meleeOnly() && source.getDirectEntity() != player) || target.getType().is(config.immune())) {
            return;
        }
        if (target.getHealth() > target.getMaxHealth() * config.threshold()) {
            return;
        }
        ItemStack stack = SigilTracker.getStack(player, SigilTracker.Kind.EXECUTIONER);
        if (!SigilTracker.drainEV(player, stack, config.evPerExecution())) {
            return;
        }
        level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.6D), target.getZ(), 12, 0.3D, 0.4D, 0.3D, 0.2D);
        level.sendParticles(ParticleTypes.SOUL, target.getX(), target.getY(0.6D), target.getZ(), 8, 0.2D, 0.3D, 0.2D, 0.02D);
        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 0.6F);
        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.WITHER_SKELETON_DEATH, SoundSource.PLAYERS, 0.6F, 1.4F);
        target.hurt(ModDamageTypes.execution(level, player), Float.MAX_VALUE);
    }
}
