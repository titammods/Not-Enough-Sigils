package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.MapCodec;
import com.titammods.animus_extended.entity.NinjaKnifeEntity;
import com.titammods.animus_extended.sigil.DemonNinjaState;
import com.titammods.animus_extended.sigil.SigilTracker;
import com.titammods.animus_extended.sigil.SpiritusAffinity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record DemonNinjaSigilEffect() implements ISigilEffect {
    public static final MapCodec<DemonNinjaSigilEffect> CODEC = MapCodec.unit(DemonNinjaSigilEffect::new);

    private static final int KNIFE_COOLDOWN = 10;
    private static final float KNIFE_VELOCITY = 2.2F;

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        NinjaKnifeEntity knife = new NinjaKnifeEntity(level, player, SpiritusAffinity.of(player));
        knife.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, KNIFE_VELOCITY, 0.5F);
        level.addFreshEntity(knife);
        player.getCooldowns().addCooldown(stack.getItem(), KNIFE_COOLDOWN);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.6F, 1.8F);
        return true;
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        SigilTracker.mark(player, SigilTracker.Kind.DEMON_NINJA, stack);
        DemonNinjaState.applyEffects(player);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
