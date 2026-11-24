package com.titammods.not_enough_sigils.sigil;

import com.titammods.not_enough_sigils.sigil.effect.BulletTimeSigilEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BulletTimeState {
    private static final String SLOWED_TAG = "not_enough_sigils_bullet_time";
    private static final int SLOW_DURATION = 20;
    private static final int PULSE_INTERVAL = 5;

    private record Active(long until, BulletTimeSigilEffect config, List<Projectile> frozen) {}

    private static final Map<UUID, Active> ACTIVE = new ConcurrentHashMap<>();

    public static void start(Player player, BulletTimeSigilEffect config) {
        Active previous = ACTIVE.put(player.getUUID(), new Active(player.level().getGameTime() + config.duration(), config, new ArrayList<>()));
        if (previous != null) {
            release(previous);
        }
    }

    public static void tick(Player player) {
        Active active = ACTIVE.get(player.getUUID());
        if (active == null) {
            return;
        }
        if (player.level().getGameTime() >= active.until() || !player.isAlive()) {
            ACTIVE.remove(player.getUUID());
            release(active);
            return;
        }

        BulletTimeSigilEffect config = active.config();
        AABB area = player.getBoundingBox().inflate(config.radius());
        double radiusSqr = config.radius() * config.radius();

        for (Projectile projectile : player.level().getEntitiesOfClass(Projectile.class, area,
                entity -> entity.isAlive() && entity.getOwner() != player && entity.distanceToSqr(player) <= radiusSqr)) {
            if (projectile.getPersistentData().getBoolean(SLOWED_TAG)) {
                continue;
            }
            projectile.getPersistentData().putBoolean(SLOWED_TAG, true);
            if (config.freezeProjectiles()) {
                active.frozen().add(projectile);
                continue;
            }
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(config.projectileSpeedFactor()));
            projectile.hasImpulse = true;
            projectile.hurtMarked = true;
        }
        for (Projectile projectile : active.frozen()) {
            if (projectile.isAlive()) {
                projectile.setNoGravity(true);
                projectile.setDeltaMovement(Vec3.ZERO);
                projectile.hurtMarked = true;
            }
        }

        if (config.slowAmplifier() < 0 || player.tickCount % PULSE_INTERVAL != 0) {
            return;
        }
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, area,
                entity -> entity.isAlive() && entity.distanceToSqr(player) <= radiusSqr)) {
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_DURATION, config.slowAmplifier(), false, true, true), player);
        }
    }

    public static void clear(UUID playerId) {
        Active active = ACTIVE.remove(playerId);
        if (active != null) {
            release(active);
        }
    }

    private static void release(Active active) {
        for (Projectile projectile : active.frozen()) {
            if (!projectile.isAlive()) {
                continue;
            }
            if (projectile instanceof AbstractHurtingProjectile) {
                projectile.discard();
                continue;
            }
            projectile.setNoGravity(false);
            projectile.setDeltaMovement(0.0D, -0.1D, 0.0D);
            projectile.hurtMarked = true;
        }
        active.frozen().clear();
    }
}
