package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.sigil.MonkMode;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import com.titammods.not_enough_sigils.sigil.config.MobEffectSpec;
import com.titammods.not_enough_sigils.sigil.config.PunchProfile;
import com.titammods.not_enough_sigils.sigil.config.SpiritusProfiles;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record SpiritualMonkSigilEffect(TagKey<Block> smeltable, Map<SpiritusType, PunchProfile> punches) implements ISigilEffect {
    public static final Map<SpiritusType, PunchProfile> DEFAULT_PUNCHES = Map.of(
            SpiritusType.RAW, new PunchProfile(1.25F, Optional.empty(), 0, 0, 0.0F, 0.0F, 0.0F),
            SpiritusType.RUINA, new PunchProfile(1.0F, Optional.of(new MobEffectSpec(MobEffects.WITHER, 100, 1)), 0, 0, 0.0F, 0.0F, 0.0F),
            SpiritusType.VINDICTA, new PunchProfile(1.0F, Optional.empty(), 0, 4, 0.0F, 0.0F, 0.0F),
            SpiritusType.INVICTUS, new PunchProfile(1.0F, Optional.of(new MobEffectSpec(MobEffects.MOVEMENT_SLOWDOWN, 60, 2)), 100, 0, 0.0F, 0.0F, 0.0F),
            SpiritusType.NIHILUM, new PunchProfile(1.0F, Optional.empty(), 0, 0, 4.0F, 0.05F, 20.0F)
    );

    public static final SpiritualMonkSigilEffect DEFAULT = new SpiritualMonkSigilEffect(Tags.Blocks.ORES, Map.of());

    public static final MapCodec<SpiritualMonkSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.codec(Registries.BLOCK).optionalFieldOf("smeltable", Tags.Blocks.ORES).forGetter(SpiritualMonkSigilEffect::smeltable),
            SpiritusProfiles.codec(PunchProfile.CODEC).optionalFieldOf("punches", Map.of()).forGetter(SpiritualMonkSigilEffect::punches)
    ).apply(instance, SpiritualMonkSigilEffect::new));

    public PunchProfile punch(SpiritusType type) {
        return SpiritusProfiles.resolve(punches, DEFAULT_PUNCHES, type, PunchProfile.NEUTRAL);
    }

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
        if (!level.isClientSide()) {
            MonkMode mode = MonkMode.get(stack).next();
            MonkMode.set(stack, mode);
            player.displayClientMessage(Component.translatable("monk_mode.not_enough_sigils.changed", mode.displayName()), true);
        }
        return true;
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        SigilTracker.mark(player, SigilTracker.Kind.SPIRITUAL_MONK, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
