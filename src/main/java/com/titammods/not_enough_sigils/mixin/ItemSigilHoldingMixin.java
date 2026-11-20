package com.titammods.not_enough_sigils.mixin;

import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.titammods.not_enough_sigils.holding.GreaterHoldingSigilItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemSigilHolding.class, remap = false)
public abstract class ItemSigilHoldingMixin {

    @Inject(method = "getInternalInventory", at = @At("HEAD"), cancellable = true)
    private static void not_enough_sigils$greaterInventory(ItemStack stack, CallbackInfoReturnable<NonNullList<ItemStack>> cir) {
        if (stack.getItem() instanceof GreaterHoldingSigilItem) {
            cir.setReturnValue(GreaterHoldingSigilItem.readInventory(stack));
        }
    }

    @Inject(method = "getCurrentItemOrdinal", at = @At("HEAD"), cancellable = true)
    private static void not_enough_sigils$greaterOrdinal(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem() instanceof GreaterHoldingSigilItem) {
            cir.setReturnValue(GreaterHoldingSigilItem.readSelected(stack));
        }
    }

    @Inject(method = "cycleToNextSigil", at = @At("HEAD"), cancellable = true)
    private static void not_enough_sigils$greaterCycle(ItemStack stack, int direction, CallbackInfo ci) {
        if (stack.getItem() instanceof GreaterHoldingSigilItem) {
            GreaterHoldingSigilItem.cycle(stack, direction);
            ci.cancel();
        }
    }
}
