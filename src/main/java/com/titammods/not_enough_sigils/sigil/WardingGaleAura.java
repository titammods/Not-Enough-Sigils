package com.titammods.not_enough_sigils.sigil;

import com.titammods.not_enough_sigils.sigil.effect.WardingGaleSigilEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WardingGaleAura {
    public static void pulse(Level level, Player player, WardingGaleSigilEffect config) {
        if (!(level instanceof ServerLevel serverLevel) || player.tickCount % Math.max(1, config.pulseInterval()) != 0) {
            return;
        }

        double radius = config.radius();
        AABB area = player.getBoundingBox().inflate(radius);
        Vec3 center = player.position();
        double radiusSqr = radius * radius;

        for (Mob mob : serverLevel.getEntitiesOfClass(Mob.class, area, entity -> entity.isAlive() && entity instanceof Enemy)) {
            Vec3 offset = mob.position().subtract(center);
            if (offset.lengthSqr() > radiusSqr) {
                continue;
            }
            Vec3 horizontal = new Vec3(offset.x, 0.0D, offset.z);
            if (horizontal.lengthSqr() < 1.0E-4D) {
                horizontal = new Vec3(1.0D, 0.0D, 0.0D);
            }
            Vec3 push = horizontal.normalize().scale(config.pushStrength());
            mob.push(push.x, config.pushLift(), push.z);
            mob.hurtMarked = true;
        }

        for (Projectile projectile : serverLevel.getEntitiesOfClass(Projectile.class, area, entity -> entity.isAlive() && entity.getOwner() != player)) {
            Vec3 motion = projectile.getDeltaMovement();
            Vec3 toPlayer = player.getBoundingBox().getCenter().subtract(projectile.position());
            if (motion.lengthSqr() < 1.0E-3D || motion.dot(toPlayer) <= 0.0D) {
                continue;
            }
            projectile.setDeltaMovement(motion.scale(config.deflectFactor()));
            projectile.hasImpulse = true;
            projectile.hurtMarked = true;
            serverLevel.sendParticles(ParticleTypes.GUST, projectile.getX(), projectile.getY(), projectile.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
