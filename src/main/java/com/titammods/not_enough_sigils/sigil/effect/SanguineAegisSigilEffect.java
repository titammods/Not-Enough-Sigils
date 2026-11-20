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

public record SanguineAegisSigilEffect(int evPerHealth) implements ISigilEffect {
    public static final int DEFAULT_EV_PER_HEALTH = 40;

    public static final SanguineAegisSigilEffect DEFAULT = new SanguineAegisSigilEffect(DEFAULT_EV_PER_HEALTH);

    public static final MapCodec<SanguineAegisSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("ev_per_health", DEFAULT_EV_PER_HEALTH).forGetter(SanguineAegisSigilEffect::evPerHealth)
    ).apply(instance, SanguineAegisSigilEffect::new));

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
        SigilTracker.mark(player, SigilTracker.Kind.SANGUINE_AEGIS, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
