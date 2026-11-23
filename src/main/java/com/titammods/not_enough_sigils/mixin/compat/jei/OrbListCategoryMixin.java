package com.titammods.not_enough_sigils.mixin.compat.jei;

import com.titammods.not_enough_sigils.setup.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = {
        "com.breakinblocks.neovitae.compat.jei.flask.FlaskCombinationCategory",
        "com.breakinblocks.neovitae.compat.jei.flask.FlaskRecipeCategory",
        "com.breakinblocks.neovitae.compat.jei.tabulavitae.TabulaVitaeRecipeCategory"
}, remap = false)
public abstract class OrbListCategoryMixin {

    @Inject(method = "getOrbsForTier(I)Ljava/util/List;", at = @At("RETURN"), require = 0)
    private void not_enough_sigils$addOrbs(int tier, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> orbs = cir.getReturnValue();
        if (orbs == null) {
            return;
        }
        for (Item orb : ModItems.bloodOrbs()) {
            orbs.add(new ItemStack(orb));
        }
    }
}
