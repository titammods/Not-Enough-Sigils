package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.animusnv.util.AnimusRitualHelper;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record RecallSigilEffect(int baseCost, double costPerBlock, int maxCost, boolean crossDimension,
                                int crossDimensionCost, int cooldown) implements ISigilEffect {
    public static final int DEFAULT_BASE_COST = 100;
    public static final double DEFAULT_COST_PER_BLOCK = 1.0D;
    public static final int DEFAULT_MAX_COST = 10000;
    public static final boolean DEFAULT_CROSS_DIMENSION = false;
    public static final int DEFAULT_CROSS_DIMENSION_COST = 5000;
    public static final int DEFAULT_COOLDOWN = 100;

    public static final RecallSigilEffect DEFAULT = new RecallSigilEffect(DEFAULT_BASE_COST, DEFAULT_COST_PER_BLOCK, DEFAULT_MAX_COST,
            DEFAULT_CROSS_DIMENSION, DEFAULT_CROSS_DIMENSION_COST, DEFAULT_COOLDOWN);

    public static final MapCodec<RecallSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("base_cost", DEFAULT_BASE_COST).forGetter(RecallSigilEffect::baseCost),
            Codec.DOUBLE.optionalFieldOf("cost_per_block", DEFAULT_COST_PER_BLOCK).forGetter(RecallSigilEffect::costPerBlock),
            Codec.INT.optionalFieldOf("max_cost", DEFAULT_MAX_COST).forGetter(RecallSigilEffect::maxCost),
            Codec.BOOL.optionalFieldOf("cross_dimension", DEFAULT_CROSS_DIMENSION).forGetter(RecallSigilEffect::crossDimension),
            Codec.INT.optionalFieldOf("cross_dimension_cost", DEFAULT_CROSS_DIMENSION_COST).forGetter(RecallSigilEffect::crossDimensionCost),
            Codec.INT.optionalFieldOf("cooldown", DEFAULT_COOLDOWN).forGetter(RecallSigilEffect::cooldown)
    ).apply(instance, RecallSigilEffect::new));

    private static final String TAG = "RecallMark";
    private static final String PREFIX = "chat.not_enough_sigils.recall.";

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
        if (player == null) {
            return false;
        }
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                mark(player, stack);
            }
            return true;
        }
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel origin)) {
            return false;
        }
        Mark mark = Mark.get(stack);
        if (mark == null) {
            fail(player, "no_mark");
            return false;
        }
        ServerLevel target = serverPlayer.server.getLevel(mark.dimension());
        if (target == null) {
            fail(player, "missing_dimension");
            return false;
        }
        boolean sameDimension = target == origin;
        if (!sameDimension && !crossDimension) {
            fail(player, "other_dimension");
            return false;
        }
        int cost = cost(player, mark, sameDimension);
        IAnima network = AnimusRitualHelper.getNetworkForBoundItem(player, stack);
        if (!player.isCreative() && (network == null || network.getCurrentEV() < cost)) {
            player.displayClientMessage(Component.translatable(PREFIX + "no_ev", cost).withStyle(ChatFormatting.RED), true);
            return false;
        }
        Vec3 destination = findSpot(target, player, mark.position());
        if (destination == null) {
            fail(player, "blocked");
            return false;
        }
        if (!player.isCreative() && network != null) {
            network.syphon(AnimaTicket.create(cost));
        }

        Vec3 start = player.position();
        origin.sendParticles(ParticleTypes.REVERSE_PORTAL, start.x, start.y + 1.0D, start.z, 40, 0.3D, 0.8D, 0.3D, 0.05D);
        origin.playSound(null, start.x, start.y, start.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 0.8F);
        serverPlayer.teleportTo(target, destination.x, destination.y, destination.z, mark.yaw(), mark.pitch());
        serverPlayer.resetFallDistance();
        target.sendParticles(ParticleTypes.PORTAL, destination.x, destination.y + 1.0D, destination.z, 40, 0.3D, 0.8D, 0.3D, 0.3D);
        target.playSound(null, destination.x, destination.y, destination.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 0.8F);
        if (cooldown > 0) {
            player.getCooldowns().addCooldown(stack.getItem(), cooldown);
        }
        player.displayClientMessage(Component.translatable(PREFIX + "teleported", cost).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return true;
    }

    private int cost(Player player, Mark mark, boolean sameDimension) {
        if (!sameDimension) {
            return Math.max(0, crossDimensionCost);
        }
        double distance = player.position().distanceTo(mark.position());
        double total = baseCost + costPerBlock * distance;
        return (int) Math.max(0, Math.min(maxCost, Math.ceil(total)));
    }

    private static Vec3 findSpot(ServerLevel level, Player player, Vec3 position) {
        level.getChunkAt(BlockPos.containing(position));
        for (int lift = 0; lift <= 3; lift++) {
            Vec3 candidate = position.add(0.0D, lift, 0.0D);
            if (level.noCollision(player.getDimensions(player.getPose()).makeBoundingBox(candidate))) {
                return candidate;
            }
        }
        return null;
    }

    private static void mark(Player player, ItemStack stack) {
        Mark mark = new Mark(player.level().dimension(), player.position(), player.getYRot(), player.getXRot());
        mark.save(stack);
        BlockPos pos = player.blockPosition();
        player.displayClientMessage(Component.translatable(PREFIX + "marked", pos.getX(), pos.getY(), pos.getZ(),
                mark.dimension().location().toString()).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        player.level().playSound(null, player.blockPosition(), SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.PLAYERS, 0.6F, 1.4F);
    }

    private static void fail(Player player, String key) {
        player.displayClientMessage(Component.translatable(PREFIX + key).withStyle(ChatFormatting.RED), true);
    }

    public record Mark(ResourceKey<Level> dimension, Vec3 position, float yaw, float pitch) {
        public static Mark get(ItemStack stack) {
            CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (!root.contains(TAG)) {
                return null;
            }
            CompoundTag tag = root.getCompound(TAG);
            ResourceLocation location = ResourceLocation.tryParse(tag.getString("dimension"));
            if (location == null) {
                return null;
            }
            return new Mark(ResourceKey.create(Registries.DIMENSION, location),
                    new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z")),
                    tag.getFloat("yaw"), tag.getFloat("pitch"));
        }

        public void save(ItemStack stack) {
            CompoundTag tag = new CompoundTag();
            tag.putString("dimension", dimension.location().toString());
            tag.putDouble("x", position.x);
            tag.putDouble("y", position.y);
            tag.putDouble("z", position.z);
            tag.putFloat("yaw", yaw);
            tag.putFloat("pitch", pitch);
            CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> root.put(TAG, tag));
        }
    }
}
