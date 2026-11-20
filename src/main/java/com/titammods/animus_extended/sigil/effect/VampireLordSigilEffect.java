package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record VampireLordSigilEffect(float lifesteal, int evPerOverkill, int maxEvPerKill) implements ISigilEffect {
    public static final float DEFAULT_LIFESTEAL = 0.15F;
    public static final int DEFAULT_EV_PER_OVERKILL = 50;
    public static final int DEFAULT_MAX_EV_PER_KILL = 5000;

    public static final VampireLordSigilEffect DEFAULT = new VampireLordSigilEffect(DEFAULT_LIFESTEAL, DEFAULT_EV_PER_OVERKILL, DEFAULT_MAX_EV_PER_KILL);

    public static final MapCodec<VampireLordSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("lifesteal", DEFAULT_LIFESTEAL).forGetter(VampireLordSigilEffect::lifesteal),
            Codec.INT.optionalFieldOf("ev_per_overkill", DEFAULT_EV_PER_OVERKILL).forGetter(VampireLordSigilEffect::evPerOverkill),
            Codec.INT.optionalFieldOf("max_ev_per_kill", DEFAULT_MAX_EV_PER_KILL).forGetter(VampireLordSigilEffect::maxEvPerKill)
    ).apply(instance, VampireLordSigilEffect::new));

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
        SigilTracker.mark(player, SigilTracker.Kind.VAMPIRE_LORD, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
