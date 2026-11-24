package com.titammods.not_enough_sigils.mixin;

import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.titammods.not_enough_sigils.altar.AscendedRunes;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = AraVitaeTile.class, remap = false)
public abstract class AraVitaeTileMixin {

    @ModifyVariable(method = "tickRecipeCrafting", at = @At("STORE"), ordinal = 1)
    private int not_enough_sigils$reduceCost(int totalRequired) {
        return AscendedRunes.applyCostReduction((BlockEntity) (Object) this, totalRequired);
    }
}
