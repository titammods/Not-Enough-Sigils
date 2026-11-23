package com.titammods.not_enough_sigils.sigil;

import com.titammods.not_enough_sigils.sigil.effect.BulletTimeSigilEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BulletTimeState {
    private static final String SLOWED_TAG = "not_enough_sigils_bullet_time";
    private static final int SLOW_DURATION = 20;
    private static final int PULSE_INTERVAL = 5;

    private record Active(long until, BulletTimeSigilEffect config) {}

    private static final Map<UUID, Active> ACTIVE = new ConcurrentHashMap<>();

    public static void start(Player player, BulletTimeSigilEffect config) {
        ACTIVE.put(player.getUUID(), new Active(player.level().getGameTime() + config.duration(), config));
    }

    public static void tick(Player player) {
        Active active = ACTIVE.get(player.getUUID());
        if (active == null) {
            return;
        }
        if (player.level().getGameTime() >= active.until() || !player.isAlive()) {
            ACTIVE.remove(player.getUUID());
            return;
        }

        BulletTimeSigilEffect config = active.config();
        AABB area = player.getBoundingBox().inflate(config.radius());
        double radiusSqr = config.radius() * config.radius();

        for (Projectile projectile : player.level().getEntitiesOfClass(Projectile.class, area,
                entity -> entity.isAlive() && entity.getOwner() != player && entity.distanceToSqr(player) <= radiusSqr)) {
            if (projectile.getPersistentData().getBooleanOr(SLOWED_TAG, false)) {
                continue;
            }
            projectile.getPersistentData().putBoolean(SLOWED_TAG, true);
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(config.projectileSpeedFactor()));
            projectile.needsSync = true;
            projectile.hurtMarked = true;
        }

        if (config.slowAmplifier() < 0 || player.tickCount % PULSE_INTERVAL != 0) {
            return;
        }
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, area,
                entity -> entity.isAlive() && entity.distanceToSqr(player) <= radiusSqr)) {
            mob.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, SLOW_DURATION, config.slowAmplifier(), false, true, true), player);
        }
    }

    public static void clear(UUID playerId) {
        ACTIVE.remove(playerId);
    }
}
