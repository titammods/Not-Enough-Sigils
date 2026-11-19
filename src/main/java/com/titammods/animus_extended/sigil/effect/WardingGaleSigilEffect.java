package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.MapCodec;
import com.titammods.animus_extended.sigil.WardingGaleAura;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record WardingGaleSigilEffect() implements ISigilEffect {
    public static final MapCodec<WardingGaleSigilEffect> CODEC = MapCodec.unit(WardingGaleSigilEffect::new);

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
        SigilTracker.mark(player, SigilTracker.Kind.WARDING_GALE, stack);
        WardingGaleAura.pulse(level, player);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
