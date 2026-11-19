package com.titammods.animus_extended.entity;

import com.titammods.animus_extended.setup.ModDamageTypes;
import com.titammods.animus_extended.setup.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagicMissileEntity extends ThrowableProjectile {
    private static final int MAX_LIFETIME = 100;
    private static final String DAMAGE_TAG = "MissileDamage";
    private static final String HOMING_TAG = "MissileHoming";
    private static final String SLOWING_TAG = "MissileSlowing";
    private static final int SLOW_TICKS = 80;
    private static final int SLOW_AMPLIFIER = 2;
    private static final double HOMING_RANGE = 16.0D;
    private static final double HOMING_STRENGTH = 0.25D;

    private float damage = 20.0F;
    private boolean homing;
    private boolean slowing;

    public MagicMissileEntity(EntityType<? extends MagicMissileEntity> type, Level level) {
        super(type, level);
    }

    public MagicMissileEntity(Level level, LivingEntity shooter, float damage, boolean homing, boolean slowing) {
        super(ModEntities.MAGIC_MISSILE.get(), shooter, level);
        this.damage = damage;
        this.homing = homing;
        this.slowing = slowing;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount > MAX_LIFETIME) {
            discard();
            return;
        }

        if (homing && !level().isClientSide() && tickCount > 3) {
            steer();
        }

        if (level().isClientSide()) {
            Vec3 motion = getDeltaMovement();
            for (int i = 0; i < 2; i++) {
                double offsetX = (random.nextDouble() - 0.5D) * 0.15D;
                double offsetY = (random.nextDouble() - 0.5D) * 0.15D;
                double offsetZ = (random.nextDouble() - 0.5D) * 0.15D;
                level().addParticle(ParticleTypes.WITCH, getX() + offsetX, getY() + offsetY, getZ() + offsetZ,
                        -motion.x * 0.05D, -motion.y * 0.05D, -motion.z * 0.05D);
            }
            level().addParticle(ParticleTypes.END_ROD, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    private void steer() {
        Entity owner = getOwner();
        Vec3 motion = getDeltaMovement();
        double speed = motion.length();
        if (speed < 1.0E-4D) {
            return;
        }
        Vec3 direction = motion.normalize();
        AABB area = getBoundingBox().inflate(HOMING_RANGE);
        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (LivingEntity candidate : level().getEntitiesOfClass(LivingEntity.class, area, entity -> entity.isAlive() && entity != owner && entity instanceof Enemy)) {
            Vec3 toTarget = candidate.getBoundingBox().getCenter().subtract(position());
            double distance = toTarget.lengthSqr();
            if (distance < bestDistance && toTarget.normalize().dot(direction) > 0.3D) {
                bestDistance = distance;
                best = candidate;
            }
        }
        if (best == null) {
            return;
        }
        Vec3 desired = best.getBoundingBox().getCenter().subtract(position()).normalize();
        Vec3 steered = direction.add(desired.subtract(direction).scale(HOMING_STRENGTH)).normalize().scale(speed);
        setDeltaMovement(steered);
        hasImpulse = true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide()) {
            return;
        }
        Entity target = result.getEntity();
        Entity owner = getOwner();
        if (target == owner || !(target instanceof LivingEntity living)) {
            return;
        }
        boolean hit = living.hurt(ModDamageTypes.arcane(level(), this, owner), damage);
        if (hit && slowing) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_TICKS, SLOW_AMPLIFIER), owner);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide()) {
            return;
        }
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, getX(), getY(), getZ(), 16, 0.25D, 0.25D, 0.25D, 0.2D);
            serverLevel.sendParticles(ParticleTypes.WITCH, getX(), getY(), getZ(), 10, 0.2D, 0.2D, 0.2D, 0.05D);
        }
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 0.8F, 1.6F);
        discard();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0D;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat(DAMAGE_TAG, damage);
        tag.putBoolean(HOMING_TAG, homing);
        tag.putBoolean(SLOWING_TAG, slowing);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(DAMAGE_TAG)) {
            damage = tag.getFloat(DAMAGE_TAG);
        }
        homing = tag.getBoolean(HOMING_TAG);
        slowing = tag.getBoolean(SLOWING_TAG);
    }
}
