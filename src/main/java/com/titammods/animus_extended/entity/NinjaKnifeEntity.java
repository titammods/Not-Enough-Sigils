package com.titammods.animus_extended.entity;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.titammods.animus_extended.setup.ModEntities;
import com.titammods.animus_extended.setup.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class NinjaKnifeEntity extends ThrowableItemProjectile {
    private static final String TYPE_TAG = "KnifeSpiritus";
    private static final int MAX_LIFETIME = 60;
    private static final float RAW_DAMAGE = 8.0F;
    private static final float ELEMENT_DAMAGE = 5.0F;
    private static final float LIGHTNING_BONUS = 5.0F;
    private static final float EXPLOSION_POWER = 1.5F;
    private static final int FROST_SLOW_TICKS = 100;
    private static final int FROST_SLOW_AMPLIFIER = 2;
    private static final int FROST_FREEZE_TICKS = 100;
    private static final int FIRE_SECONDS = 5;

    private SpiritusType spiritusType = SpiritusType.RAW;

    public NinjaKnifeEntity(EntityType<? extends NinjaKnifeEntity> type, Level level) {
        super(type, level);
    }

    public NinjaKnifeEntity(Level level, LivingEntity thrower, SpiritusType spiritusType) {
        super(ModEntities.NINJA_KNIFE.get(), thrower, level);
        this.spiritusType = spiritusType;
        this.setItem(new ItemStack(itemFor(spiritusType)));
    }

    public static Item itemFor(SpiritusType type) {
        return switch (type) {
            case NIHILUM -> ModItems.NINJA_KNIFE_EXPLOSIVE.get();
            case RUINA -> ModItems.NINJA_KNIFE_LIGHTNING.get();
            case INVICTUS -> ModItems.NINJA_KNIFE_FROST.get();
            case VINDICTA -> ModItems.NINJA_KNIFE_FIRE.get();
            default -> ModItems.NINJA_KNIFE.get();
        };
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.NINJA_KNIFE.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.01D;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > MAX_LIFETIME) {
            discard();
            return;
        }
        if (level().isClientSide() && tickCount % 2 == 0) {
            level().addParticle(ParticleTypes.CRIT, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide()) {
            return;
        }
        Entity target = result.getEntity();
        Entity owner = getOwner();
        if (target == owner) {
            return;
        }

        float damage = spiritusType == SpiritusType.RAW ? RAW_DAMAGE : ELEMENT_DAMAGE;
        boolean hit = target.hurt(level().damageSources().thrown(this, owner), damage);
        if (!hit || !(target instanceof LivingEntity living)) {
            return;
        }

        switch (spiritusType) {
            case RUINA -> strikeLightning(living, owner);
            case INVICTUS -> {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, FROST_SLOW_TICKS, FROST_SLOW_AMPLIFIER), owner);
                if (living.canFreeze()) {
                    living.setTicksFrozen(Math.max(living.getTicksFrozen(), living.getTicksRequiredToFreeze() + FROST_FREEZE_TICKS));
                }
            }
            case VINDICTA -> living.igniteForSeconds(FIRE_SECONDS);
            default -> {
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide()) {
            return;
        }
        if (spiritusType == SpiritusType.NIHILUM) {
            level().explode(this, getX(), getY(), getZ(), EXPLOSION_POWER, Level.ExplosionInteraction.NONE);
        } else {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.ARROW_HIT, SoundSource.PLAYERS, 0.6F, 1.6F);
        }
        discard();
    }

    private void strikeLightning(LivingEntity target, Entity owner) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
        if (bolt != null) {
            bolt.moveTo(target.getX(), target.getY(), target.getZ());
            bolt.setVisualOnly(true);
            if (owner instanceof ServerPlayer player) {
                bolt.setCause(player);
            }
            serverLevel.addFreshEntity(bolt);
        }
        target.invulnerableTime = 0;
        target.hurt(serverLevel.damageSources().lightningBolt(), LIGHTNING_BONUS);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString(TYPE_TAG, spiritusType.getSerializedName());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TYPE_TAG)) {
            String name = tag.getString(TYPE_TAG);
            for (SpiritusType type : SpiritusType.values()) {
                if (type.getSerializedName().equals(name)) {
                    spiritusType = type;
                    break;
                }
            }
        }
    }
}
