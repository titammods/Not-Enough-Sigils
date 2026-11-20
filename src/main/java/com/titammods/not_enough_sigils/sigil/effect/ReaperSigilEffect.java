package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record ReaperSigilEffect(double spiritusPerHealth, double maxSpiritusPerKill, float xpMultiplier, float xpPerLooting) implements ISigilEffect {
    public static final double DEFAULT_SPIRITUS_PER_HEALTH = 0.075D;
    public static final double DEFAULT_MAX_SPIRITUS_PER_KILL = 96.0D;
    public static final float DEFAULT_XP_MULTIPLIER = 1.5F;
    public static final float DEFAULT_XP_PER_LOOTING = 0.25F;

    public static final ReaperSigilEffect DEFAULT = new ReaperSigilEffect(DEFAULT_SPIRITUS_PER_HEALTH, DEFAULT_MAX_SPIRITUS_PER_KILL, DEFAULT_XP_MULTIPLIER, DEFAULT_XP_PER_LOOTING);

    public static final MapCodec<ReaperSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("spiritus_per_health", DEFAULT_SPIRITUS_PER_HEALTH).forGetter(ReaperSigilEffect::spiritusPerHealth),
            Codec.DOUBLE.optionalFieldOf("max_spiritus_per_kill", DEFAULT_MAX_SPIRITUS_PER_KILL).forGetter(ReaperSigilEffect::maxSpiritusPerKill),
            Codec.FLOAT.optionalFieldOf("xp_multiplier", DEFAULT_XP_MULTIPLIER).forGetter(ReaperSigilEffect::xpMultiplier),
            Codec.FLOAT.optionalFieldOf("xp_per_looting", DEFAULT_XP_PER_LOOTING).forGetter(ReaperSigilEffect::xpPerLooting)
    ).apply(instance, ReaperSigilEffect::new));

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        SigilTracker.mark(player, SigilTracker.Kind.REAPER, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
