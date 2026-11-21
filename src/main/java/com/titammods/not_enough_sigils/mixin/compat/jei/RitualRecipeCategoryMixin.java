package com.titammods.not_enough_sigils.mixin.compat.jei;

import com.breakinblocks.neovitae.compat.jei.ritual.RitualJEIRecipe;
import com.titammods.not_enough_sigils.setup.ModItems;
import com.titammods.not_enough_sigils.setup.ModRituals;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.breakinblocks.neovitae.compat.jei.ritual.RitualRecipeCategory", remap = false)
public abstract class RitualRecipeCategoryMixin {
    private static final int OUTPUT_X = 8;
    private static final int OUTPUT_Y = 80;

    @Inject(
            method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/breakinblocks/neovitae/compat/jei/ritual/RitualJEIRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At("HEAD"),
            require = 0
    )
    private void not_enough_sigils$addSaerethOutput(IRecipeLayoutBuilder builder, RitualJEIRecipe recipe, IFocusGroup focuses, CallbackInfo ci) {
        if (ModRituals.SAERETH.getId().equals(recipe.ritualId())) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y).addItemStack(new ItemStack(ModItems.SAERETH_SIGIL.get()));
        }
    }
}
