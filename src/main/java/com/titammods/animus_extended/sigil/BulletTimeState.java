package com.titammods.animus_extended.sigil;

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
    private static final String SLOWED_TAG = "animus_extended_bullet_time";
    private static final double RADIUS = 16.0D;
    private static final double PROJECTILE_SPEED_FACTOR = 0.2D;
    private static final int SLOW_DURATION = 20;
    private static final int SLOW_AMPLIFIER = 4;
    private static final int PULSE_INTERVAL = 5;

    private static final Map<UUID, Long> ACTIVE_UNTIL = new ConcurrentHashMap<>();

    public static void start(Player player, int duration) {
        ACTIVE_UNTIL.put(player.getUUID(), player.level().getGameTime() + duration);
    }

    public static void tick(Player player) {
        Long until = ACTIVE_UNTIL.get(player.getUUID());
        if (until == null) {
            return;
        }
        if (player.level().getGameTime() >= until || !player.isAlive()) {
            ACTIVE_UNTIL.remove(player.getUUID());
            return;
        }

        AABB area = player.getBoundingBox().inflate(RADIUS);
        double radiusSqr = RADIUS * RADIUS;

        for (Projectile projectile : player.level().getEntitiesOfClass(Projectile.class, area,
                entity -> entity.isAlive() && entity.getOwner() != player && entity.distanceToSqr(player) <= radiusSqr)) {
            if (projectile.getPersistentData().getBoolean(SLOWED_TAG)) {
                continue;
            }
            projectile.getPersistentData().putBoolean(SLOWED_TAG, true);
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(PROJECTILE_SPEED_FACTOR));
            projectile.hasImpulse = true;
            projectile.hurtMarked = true;
        }

        if (player.tickCount % PULSE_INTERVAL != 0) {
            return;
        }
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, area,
                entity -> entity.isAlive() && entity.distanceToSqr(player) <= radiusSqr)) {
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_DURATION, SLOW_AMPLIFIER, false, true, true), player);
        }
    }

    public static void clear(UUID playerId) {
        ACTIVE_UNTIL.remove(playerId);
    }
}
