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

public record SoulPhoenixSigilEffect(boolean greater) implements ISigilEffect {
    public static final MapCodec<SoulPhoenixSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("greater", false).forGetter(SoulPhoenixSigilEffect::greater)
    ).apply(instance, SoulPhoenixSigilEffect::new));

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
        SigilTracker.mark(player, greater ? SigilTracker.Kind.SOUL_PHOENIX_GREATER : SigilTracker.Kind.SOUL_PHOENIX, stack);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
