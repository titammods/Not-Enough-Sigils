package com.titammods.not_enough_sigils.sigil;

import com.titammods.not_enough_sigils.sigil.effect.SoulPhoenixSigilEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PhoenixGuard {
    private static final Map<UUID, Long> GRACE_UNTIL = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> GRACE_LENGTH = new ConcurrentHashMap<>();
    private static final Set<UUID> PENDING_VOID_ESCAPE = ConcurrentHashMap.newKeySet();
    private static final ThreadLocal<Boolean> REVIVING = ThreadLocal.withInitial(() -> false);

    public static boolean isReviving() {
        return REVIVING.get();
    }

    public static boolean inGrace(ServerPlayer player) {
        Long until = GRACE_UNTIL.get(player.getUUID());
        if (until == null) {
            return false;
        }
        if (player.level().getGameTime() >= until) {
            GRACE_UNTIL.remove(player.getUUID());
            return false;
        }
        return true;
    }

    public static boolean tryGreaterRevive(ServerPlayer player, boolean fromVoid) {
        return tryRevive(player, SigilTracker.Kind.SOUL_PHOENIX_GREATER, SoulPhoenixSigilEffect.GREATER, fromVoid);
    }

    public static boolean tryLesserRevive(ServerPlayer player) {
        return tryRevive(player, SigilTracker.Kind.SOUL_PHOENIX, SoulPhoenixSigilEffect.LESSER, false);
    }

    private static boolean tryRevive(ServerPlayer player, SigilTracker.Kind kind, SoulPhoenixSigilEffect fallback, boolean fromVoid) {
        ItemStack sigil = SigilTracker.getStack(player, kind);
        if (sigil.isEmpty()) {
            return false;
        }
        SoulPhoenixSigilEffect config = SigilTracker.getEffect(player, kind, SoulPhoenixSigilEffect.class);
        if (config == null) {
            config = fallback;
        }
        if (player.getCooldowns().isOnCooldown(sigil) || !SigilTracker.drainEV(player, sigil, config.evCost())) {
            return false;
        }
        float health = Math.max(config.reviveHealth(), player.getMaxHealth() * config.reviveHealthRatio());
        revive(player, sigil, config.cooldown(), health, config.invulnerability());
        if (config.graceTicks() > 0) {
            GRACE_UNTIL.put(player.getUUID(), player.level().getGameTime() + config.graceTicks());
            GRACE_LENGTH.put(player.getUUID(), (long) config.graceTicks());
        }
        if (config.voidEscape() && (fromVoid || player.getY() < player.level().getMinY())) {
            PENDING_VOID_ESCAPE.add(player.getUUID());
        }
        return true;
    }

    private static void revive(ServerPlayer player, ItemStack sigil, int cooldown, float health, int invulnerability) {
        REVIVING.set(true);
        try {
            player.setHealth(Math.max(1.0F, Math.min(player.getMaxHealth(), health)));
        } finally {
            REVIVING.set(false);
        }
        player.invulnerableTime = invulnerability;
        player.getCooldowns().addCooldown(sigil, cooldown);
        player.sendOverlayMessage(Component.translatable("message.not_enough_sigils.soul_phoenix.revived"));

        ServerLevel level = (ServerLevel) player.level();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.2F);
        level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 1.0, player.getZ(), 40, 0.5, 0.8, 0.5, 0.08);
        level.sendParticles(ParticleTypes.LAVA, player.getX(), player.getY() + 1.0, player.getZ(), 12, 0.4, 0.4, 0.4, 0.0);
    }

    public static void tick(ServerPlayer player) {
        if (!PENDING_VOID_ESCAPE.remove(player.getUUID())) {
            return;
        }
        MinecraftServer server = ((ServerLevel) player.level()).getServer();
        ServerPlayer.RespawnConfig respawn = player.getRespawnConfig();
        ServerLevel target = respawn == null || respawn.respawnData() == null ? null : server.getLevel(respawn.respawnData().dimension());
        BlockPos anchor = target == null ? null : respawn.respawnData().pos();
        if (target == null || anchor == null) {
            target = server.overworld();
            anchor = target.getLevelData().getRespawnData().pos();
        }
        BlockPos safe = target.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, anchor);
        if (safe.getY() <= target.getMinY()) {
            safe = new BlockPos(anchor.getX(), Math.max(anchor.getY(), target.getSeaLevel()), anchor.getZ());
        }
        player.teleportTo(target, safe.getX() + 0.5D, safe.getY(), safe.getZ() + 0.5D, Set.<Relative>of(), player.getYRot(), player.getXRot(), true);
        player.resetFallDistance();
        long grace = GRACE_LENGTH.getOrDefault(player.getUUID(), 0L);
        if (grace > 0L) {
            GRACE_UNTIL.put(player.getUUID(), player.level().getGameTime() + grace);
        }
    }

    public static void clear(UUID playerId) {
        GRACE_UNTIL.remove(playerId);
        GRACE_LENGTH.remove(playerId);
        PENDING_VOID_ESCAPE.remove(playerId);
    }
}
