package com.titammods.animus_extended.sigil;

import com.titammods.animus_extended.setup.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PhoenixGuard {
    public static final int REVIVE_EV_COST = 1000;
    private static final int COOLDOWN_TICKS = 300;
    private static final int GREATER_COOLDOWN_TICKS = 160;
    private static final float REVIVE_HEALTH = 4.0F;
    private static final float GREATER_REVIVE_RATIO = 0.5F;
    private static final int REVIVE_INVULNERABILITY = 20;
    private static final long GREATER_GRACE_TICKS = 20L;

    private static final Map<UUID, Long> GRACE_UNTIL = new ConcurrentHashMap<>();
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
        ItemStack sigil = SigilTracker.getStack(player, SigilTracker.Kind.SOUL_PHOENIX_GREATER);
        if (sigil.isEmpty()) {
            return false;
        }
        Item item = ModItems.SIGIL_SOUL_PHOENIX_GREATER.get();
        if (player.getCooldowns().isOnCooldown(item) || !SigilTracker.drainEV(player, sigil, REVIVE_EV_COST)) {
            return false;
        }
        revive(player, item, GREATER_COOLDOWN_TICKS, player.getMaxHealth() * GREATER_REVIVE_RATIO);
        GRACE_UNTIL.put(player.getUUID(), player.level().getGameTime() + GREATER_GRACE_TICKS);
        if (fromVoid || player.getY() < player.level().getMinBuildHeight()) {
            PENDING_VOID_ESCAPE.add(player.getUUID());
        }
        return true;
    }

    public static boolean tryLesserRevive(ServerPlayer player) {
        ItemStack sigil = SigilTracker.getStack(player, SigilTracker.Kind.SOUL_PHOENIX);
        if (sigil.isEmpty()) {
            return false;
        }
        Item item = ModItems.SIGIL_SOUL_PHOENIX.get();
        if (player.getCooldowns().isOnCooldown(item) || !SigilTracker.drainEV(player, sigil, REVIVE_EV_COST)) {
            return false;
        }
        revive(player, item, COOLDOWN_TICKS, REVIVE_HEALTH);
        return true;
    }

    private static void revive(ServerPlayer player, Item item, int cooldown, float health) {
        REVIVING.set(true);
        try {
            player.setHealth(Math.max(1.0F, Math.min(player.getMaxHealth(), health)));
        } finally {
            REVIVING.set(false);
        }
        player.invulnerableTime = REVIVE_INVULNERABILITY;
        player.getCooldowns().addCooldown(item, cooldown);
        player.displayClientMessage(Component.translatable("message.animus_extended.soul_phoenix.revived"), true);

        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.2F);
        level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 1.0, player.getZ(), 40, 0.5, 0.8, 0.5, 0.08);
        level.sendParticles(ParticleTypes.LAVA, player.getX(), player.getY() + 1.0, player.getZ(), 12, 0.4, 0.4, 0.4, 0.0);
    }

    public static void tick(ServerPlayer player) {
        if (!PENDING_VOID_ESCAPE.remove(player.getUUID())) {
            return;
        }
        ServerLevel target = player.server.getLevel(player.getRespawnDimension());
        BlockPos anchor = player.getRespawnPosition();
        if (target == null || anchor == null) {
            target = player.server.overworld();
            anchor = target.getSharedSpawnPos();
        }
        BlockPos safe = target.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, anchor);
        if (safe.getY() <= target.getMinBuildHeight()) {
            safe = new BlockPos(anchor.getX(), Math.max(anchor.getY(), target.getSeaLevel()), anchor.getZ());
        }
        player.teleportTo(target, safe.getX() + 0.5D, safe.getY(), safe.getZ() + 0.5D, player.getYRot(), player.getXRot());
        player.resetFallDistance();
        GRACE_UNTIL.put(player.getUUID(), player.level().getGameTime() + GREATER_GRACE_TICKS);
    }

    public static void clear(UUID playerId) {
        GRACE_UNTIL.remove(playerId);
        PENDING_VOID_ESCAPE.remove(playerId);
    }
}
