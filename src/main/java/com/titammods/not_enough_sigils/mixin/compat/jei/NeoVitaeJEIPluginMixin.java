package com.titammods.not_enough_sigils.mixin.compat.jei;

import com.titammods.not_enough_sigils.setup.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(targets = "com.breakinblocks.neovitae.compat.jei.NeoVitaeJEIPlugin", remap = false)
public abstract class NeoVitaeJEIPluginMixin {

    @Inject(method = "registerRecipes(Lmezz/jei/api/registration/IRecipeRegistration;)V", at = @At("TAIL"), require = 0)
    private void not_enough_sigils$addOrbInfo(IRecipeRegistration registration, CallbackInfo ci) {
        List<ItemStack> orbs = new ArrayList<>();
        for (Item orb : ModItems.bloodOrbs()) {
            orbs.add(new ItemStack(orb));
        }
        registration.addIngredientInfo(orbs, VanillaTypes.ITEM_STACK, Component.translatable("jei.neovitae.orb.info"));
    }
}
