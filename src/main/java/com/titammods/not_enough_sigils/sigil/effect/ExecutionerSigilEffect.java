package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

import java.util.UUID;

public record ExecutionerSigilEffect(float threshold, int evPerExecution, boolean meleeOnly, TagKey<EntityType<?>> immune) implements ISigilEffect {
    public static final float DEFAULT_THRESHOLD = 0.15F;
    public static final int DEFAULT_EV_PER_EXECUTION = 50;
    public static final boolean DEFAULT_MELEE_ONLY = true;
    public static final TagKey<EntityType<?>> DEFAULT_IMMUNE = Tags.EntityTypes.BOSSES;

    public static final ExecutionerSigilEffect DEFAULT = new ExecutionerSigilEffect(DEFAULT_THRESHOLD, DEFAULT_EV_PER_EXECUTION, DEFAULT_MELEE_ONLY, DEFAULT_IMMUNE);

    public static final MapCodec<ExecutionerSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("threshold", DEFAULT_THRESHOLD).forGetter(ExecutionerSigilEffect::threshold),
            Codec.INT.optionalFieldOf("ev_per_execution", DEFAULT_EV_PER_EXECUTION).forGetter(ExecutionerSigilEffect::evPerExecution),
            Codec.BOOL.optionalFieldOf("melee_only", DEFAULT_MELEE_ONLY).forGetter(ExecutionerSigilEffect::meleeOnly),
            TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("immune", DEFAULT_IMMUNE).forGetter(ExecutionerSigilEffect::immune)
    ).apply(instance, ExecutionerSigilEffect::new));

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
        SigilTracker.mark(player, SigilTracker.Kind.EXECUTIONER, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
