package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.spiritus.IPlayerSpiritusHandler;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.animus_extended.entity.MagicMissileEntity;
import com.titammods.animus_extended.sigil.SpiritusAffinity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record MagicianSigilEffect(int missiles, boolean homing, int cooldown) implements ISigilEffect {
    public static final MapCodec<MagicianSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("missiles", 1).forGetter(MagicianSigilEffect::missiles),
            Codec.BOOL.optionalFieldOf("homing", false).forGetter(MagicianSigilEffect::homing),
            Codec.INT.optionalFieldOf("cooldown", 20).forGetter(MagicianSigilEffect::cooldown)
    ).apply(instance, MagicianSigilEffect::new));

    public static final float BASE_DAMAGE = 20.0F;
    public static final float MAX_BONUS_DAMAGE = 20.0F;
    public static final double MAX_SPIRITUS = 4096.0D;
    private static final float VELOCITY = 2.5F;
    private static final float SPREAD_DEGREES = 10.0F;

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }

        SpiritusType affinity = SpiritusAffinity.of(player);
        float damage = computeDamage(player) * damageMultiplier(affinity);
        float velocity = VELOCITY * velocityMultiplier(affinity);
        boolean seeking = homing || affinity == SpiritusType.RUINA;
        boolean slowing = affinity == SpiritusType.NIHILUM;
        int count = Math.max(1, missiles);
        for (int i = 0; i < count; i++) {
            float yawOffset = (i - (count - 1) / 2.0F) * SPREAD_DEGREES;
            MagicMissileEntity missile = new MagicMissileEntity(level, player, damage, seeking, slowing);
            missile.shootFromRotation(player, player.getXRot(), player.getYRot() + yawOffset, 0.0F, velocity, 0.0F);
            level.addFreshEntity(missile);
        }
        player.getCooldowns().addCooldown(stack.getItem(), Math.max(1, cooldown));
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 0.8F, 1.4F);
        return true;
    }

    public static float damageMultiplier(SpiritusType type) {
        return switch (type) {
            case RUINA, INVICTUS -> 0.75F;
            case VINDICTA -> 1.4F;
            case NIHILUM -> 0.7F;
            default -> 1.0F;
        };
    }

    public static float velocityMultiplier(SpiritusType type) {
        return switch (type) {
            case VINDICTA -> 0.6F;
            case INVICTUS -> 1.6F;
            default -> 1.0F;
        };
    }

    public static float computeDamage(Player player) {
        IPlayerSpiritusHandler handler = NeoVitaeAPI.getInstance().getPlayerSpiritusHandler();
        double total = 0.0D;
        for (SpiritusType type : SpiritusType.values()) {
            total += handler.getTotalSpiritus(type, player);
        }
        double ratio = Math.min(total, MAX_SPIRITUS) / MAX_SPIRITUS;
        return BASE_DAMAGE + (float) (ratio * MAX_BONUS_DAMAGE);
    }
}
