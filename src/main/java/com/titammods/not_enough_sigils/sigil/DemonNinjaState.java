package com.titammods.not_enough_sigils.sigil;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DemonNinjaState {
    private static final int NIGHT_VISION_TICKS = 400;
    private static final int NIGHT_VISION_REFRESH = 220;
    private static final int INVISIBILITY_TICKS = 60;
    private static final int INVISIBILITY_REFRESH = 30;

    private static final Map<UUID, Long> REVEALED_UNTIL = new ConcurrentHashMap<>();
    private static final Set<UUID> APPLIED = ConcurrentHashMap.newKeySet();

    public static void applyEffects(Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        APPLIED.add(player.getUUID());
        refresh(player, MobEffects.NIGHT_VISION, NIGHT_VISION_TICKS, NIGHT_VISION_REFRESH);
        if (isRevealed(player)) {
            return;
        }
        refresh(player, MobEffects.INVISIBILITY, INVISIBILITY_TICKS, INVISIBILITY_REFRESH);
    }

    public static void reveal(Player player, int revealTicks) {
        if (revealTicks <= 0) {
            return;
        }
        REVEALED_UNTIL.put(player.getUUID(), player.level().getGameTime() + revealTicks);
        removeOwnEffect(player, MobEffects.INVISIBILITY, INVISIBILITY_TICKS);
    }

    public static boolean isRevealed(Player player) {
        Long until = REVEALED_UNTIL.get(player.getUUID());
        if (until == null) {
            return false;
        }
        if (player.level().getGameTime() >= until) {
            REVEALED_UNTIL.remove(player.getUUID());
            return false;
        }
        return true;
    }

    public static void clearIfInactive(Player player) {
        if (!APPLIED.contains(player.getUUID())) {
            return;
        }
        if (SigilTracker.isActive(player, SigilTracker.Kind.DEMON_NINJA)) {
            return;
        }
        APPLIED.remove(player.getUUID());
        removeOwnEffect(player, MobEffects.NIGHT_VISION, NIGHT_VISION_TICKS);
        removeOwnEffect(player, MobEffects.INVISIBILITY, INVISIBILITY_TICKS);
    }

    public static void clear(UUID playerId) {
        REVEALED_UNTIL.remove(playerId);
        APPLIED.remove(playerId);
    }

    private static void refresh(Player player, Holder<MobEffect> effect, int duration, int refreshBelow) {
        MobEffectInstance current = player.getEffect(effect);
        if (current != null && (current.isInfiniteDuration() || current.getDuration() > refreshBelow)) {
            return;
        }
        player.addEffect(new MobEffectInstance(effect, duration, 0, true, false, true));
    }

    private static void removeOwnEffect(Player player, Holder<MobEffect> effect, int maxDuration) {
        MobEffectInstance current = player.getEffect(effect);
        if (current != null && current.getAmplifier() == 0 && !current.isInfiniteDuration() && current.getDuration() <= maxDuration) {
            player.removeEffect(effect);
        }
    }
}
