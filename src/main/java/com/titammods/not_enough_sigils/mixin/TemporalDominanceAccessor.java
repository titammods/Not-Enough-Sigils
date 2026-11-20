package com.titammods.not_enough_sigils.mixin;

import com.breakinblocks.animusnv.items.sigils.effects.TemporalDominanceSigilEffect;
import net.minecraft.core.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = TemporalDominanceSigilEffect.class, remap = false)
public interface TemporalDominanceAccessor {

    @Accessor("acceleratedBlocks")
    static Map<GlobalPos, TemporalDominanceSigilEffect.AccelerationState> not_enough_sigils$getAcceleratedBlocks() {
        throw new AssertionError();
    }
}
