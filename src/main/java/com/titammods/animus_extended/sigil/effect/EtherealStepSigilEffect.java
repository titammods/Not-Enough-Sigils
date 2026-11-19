package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public record EtherealStepSigilEffect() implements ISigilEffect {
    public static final MapCodec<EtherealStepSigilEffect> CODEC = MapCodec.unit(EtherealStepSigilEffect::new);

    private static final double RANGE = 16.0D;
    private static final double WALL_PADDING = 0.6D;
    private static final double STEP = 0.5D;
    private static final int COOLDOWN_TICKS = 60;

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        return blink(level, player, stack);
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos blockPos, Direction side, Vec3 hitVec) {
        return blink(level, player, stack);
    }

    private boolean blink(Level level, Player player, ItemStack stack) {
        if (player == null || player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(RANGE));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double distance = hit.getType() == HitResult.Type.MISS ? RANGE : hit.getLocation().distanceTo(eye) - WALL_PADDING;

        Vec3 destination = null;
        for (double d = distance; d >= 1.0D && destination == null; d -= STEP) {
            Vec3 point = eye.add(look.scale(d));
            for (double lift = 0.0D; lift <= 1.5D; lift += STEP) {
                Vec3 candidate = point.add(0.0D, lift, 0.0D);
                if (level.noCollision(player, player.getBoundingBox().move(candidate.subtract(player.position())))) {
                    destination = candidate;
                    break;
                }
            }
        }
        if (destination == null) {
            return false;
        }

        Vec3 origin = player.position();
        serverPlayer.teleportTo(destination.x, destination.y, destination.z);
        serverPlayer.resetFallDistance();
        player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TICKS);

        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, origin.x, origin.y + 1.0D, origin.z, 30, 0.3D, 0.6D, 0.3D, 0.05D);
        serverLevel.sendParticles(ParticleTypes.SOUL, destination.x, destination.y + 1.0D, destination.z, 15, 0.3D, 0.6D, 0.3D, 0.02D);
        level.playSound(null, origin.x, origin.y, origin.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.7F, 1.4F);
        level.playSound(null, destination.x, destination.y, destination.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.7F, 1.4F);
        return true;
    }
}
