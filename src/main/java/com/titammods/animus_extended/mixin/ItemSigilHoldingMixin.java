package com.titammods.animus_extended.mixin;

import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.titammods.animus_extended.holding.GreaterHoldingSigilItem;
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
    private static void animus_extended$greaterInventory(ItemStack stack, CallbackInfoReturnable<NonNullList<ItemStack>> cir) {
        if (stack.getItem() instanceof GreaterHoldingSigilItem) {
            cir.setReturnValue(GreaterHoldingSigilItem.readInventory(stack));
        }
    }

    @Inject(method = "getCurrentItemOrdinal", at = @At("HEAD"), cancellable = true)
    private static void animus_extended$greaterOrdinal(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem() instanceof GreaterHoldingSigilItem) {
            cir.setReturnValue(GreaterHoldingSigilItem.readSelected(stack));
        }
    }

    @Inject(method = "cycleToNextSigil", at = @At("HEAD"), cancellable = true)
    private static void animus_extended$greaterCycle(ItemStack stack, int direction, CallbackInfo ci) {
        if (stack.getItem() instanceof GreaterHoldingSigilItem) {
            GreaterHoldingSigilItem.cycle(stack, direction);
            ci.cancel();
        }
    }
}
