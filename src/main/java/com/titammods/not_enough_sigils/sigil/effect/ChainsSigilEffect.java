package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

public record ChainsSigilEffect(double range, double pullFactor, double maxSpeed, double lift, int cooldown, int fallGraceTicks,
                                TagKey<EntityType<?>> immune) implements ISigilEffect {
    public static final double DEFAULT_RANGE = 24.0D;
    public static final double DEFAULT_PULL_FACTOR = 0.12D;
    public static final double DEFAULT_MAX_SPEED = 2.5D;
    public static final double DEFAULT_LIFT = 0.3D;
    public static final int DEFAULT_COOLDOWN = 20;
    public static final int DEFAULT_FALL_GRACE_TICKS = 60;
    public static final TagKey<EntityType<?>> DEFAULT_IMMUNE = Tags.EntityTypes.BOSSES;

    public static final ChainsSigilEffect DEFAULT = new ChainsSigilEffect(DEFAULT_RANGE, DEFAULT_PULL_FACTOR, DEFAULT_MAX_SPEED,
            DEFAULT_LIFT, DEFAULT_COOLDOWN, DEFAULT_FALL_GRACE_TICKS, DEFAULT_IMMUNE);

    public static final MapCodec<ChainsSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("range", DEFAULT_RANGE).forGetter(ChainsSigilEffect::range),
            Codec.DOUBLE.optionalFieldOf("pull_factor", DEFAULT_PULL_FACTOR).forGetter(ChainsSigilEffect::pullFactor),
            Codec.DOUBLE.optionalFieldOf("max_speed", DEFAULT_MAX_SPEED).forGetter(ChainsSigilEffect::maxSpeed),
            Codec.DOUBLE.optionalFieldOf("lift", DEFAULT_LIFT).forGetter(ChainsSigilEffect::lift),
            Codec.INT.optionalFieldOf("cooldown", DEFAULT_COOLDOWN).forGetter(ChainsSigilEffect::cooldown),
            Codec.INT.optionalFieldOf("fall_grace_ticks", DEFAULT_FALL_GRACE_TICKS).forGetter(ChainsSigilEffect::fallGraceTicks),
            TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("immune", DEFAULT_IMMUNE).forGetter(ChainsSigilEffect::immune)
    ).apply(instance, ChainsSigilEffect::new));

    public static final String FALL_GRACE = "not_enough_sigils:chains_fall_grace";

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        return use(level, player, stack);
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos blockPos, Direction side, Vec3 hitVec) {
        return use(level, player, stack);
    }

    private boolean use(Level level, Player player, ItemStack stack) {
        if (player == null || player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(range));
        BlockHitResult blockHit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 limit = blockHit.getType() == HitResult.Type.MISS ? end : blockHit.getLocation();
        AABB area = player.getBoundingBox().expandTowards(limit.subtract(eye)).inflate(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player, eye, limit, area,
                entity -> entity instanceof LivingEntity && entity.isAlive() && !entity.isSpectator() && entity.isPickable(),
                eye.distanceToSqr(limit));

        Vec3 anchor;
        Entity target = entityHit == null ? null : entityHit.getEntity();
        if (player.isShiftKeyDown()) {
            if (target != null) {
                anchor = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            } else if (blockHit.getType() == HitResult.Type.BLOCK) {
                anchor = blockHit.getLocation();
            } else {
                return false;
            }
        } else {
            if (target == null || target.getType().is(immune)) {
                return false;
            }
            anchor = null;
        }
        if (level.isClientSide()) {
            return true;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (anchor != null) {
            Vec3 velocity = velocity(player.position(), anchor);
            player.setDeltaMovement(velocity);
            player.hurtMarked = true;
            player.resetFallDistance();
            player.getPersistentData().putLong(FALL_GRACE, level.getGameTime() + fallGraceTicks);
            drawChain(serverLevel, eye, anchor);
        } else {
            Vec3 velocity = velocity(target.position(), player.position());
            target.setDeltaMovement(velocity);
            target.hurtMarked = true;
            target.resetFallDistance();
            drawChain(serverLevel, eye, target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D));
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 1.0F, 0.7F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.PLAYERS, 1.0F, 0.8F);
        if (cooldown > 0) {
            player.getCooldowns().addCooldown(stack.getItem(), cooldown);
        }
        return true;
    }

    private Vec3 velocity(Vec3 from, Vec3 to) {
        Vec3 delta = to.subtract(from);
        double distance = delta.length();
        if (distance < 1.0E-4D) {
            return Vec3.ZERO;
        }
        double speed = Math.min(maxSpeed, 0.5D + distance * pullFactor);
        return delta.normalize().scale(speed).add(0.0D, lift, 0.0D);
    }

    private static void drawChain(ServerLevel level, Vec3 from, Vec3 to) {
        Vec3 delta = to.subtract(from);
        int steps = (int) Math.ceil(delta.length() * 2.0D);
        for (int i = 1; i <= steps; i++) {
            Vec3 point = from.add(delta.scale((double) i / steps));
            level.sendParticles(ParticleTypes.CRIT, point.x, point.y, point.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
