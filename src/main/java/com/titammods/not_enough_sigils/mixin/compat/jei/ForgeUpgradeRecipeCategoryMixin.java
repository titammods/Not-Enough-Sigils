package com.titammods.not_enough_sigils.mixin.compat.jei;

import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.llamalad7.mixinextras.sugar.Local;
import com.titammods.not_enough_sigils.util.ExtendedSpiritusGems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Pseudo
@Mixin(targets = "com.breakinblocks.neovitae.compat.jei.forge.ForgeUpgradeRecipeCategory", remap = false)
public abstract class ForgeUpgradeRecipeCategoryMixin {

    @ModifyArg(
            method = "setRecipe",
            at = @At(value = "INVOKE", target = "addItemStacks", ordinal = 0),
            index = 0,
            require = 0
    )
    private List<ItemStack> not_enough_sigils$addExtendedGems(List<ItemStack> validGems, @Local(argsOnly = true) ForgeRecipe recipe) {
        return ExtendedSpiritusGems.withExtended(validGems, recipe.getMinSpiritus());
    }
}
