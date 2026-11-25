package com.titammods.not_enough_sigils.mixin;

import com.titammods.not_enough_sigils.util.ExperienceHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @Unique
    private long not_enough_sigils$pointsBefore = -1L;
    @Unique
    private int not_enough_sigils$cost;

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true, require = 0)
    private void not_enough_sigils$payWithPoints(Player player, boolean hasStack, CallbackInfoReturnable<Boolean> cir) {
        int cost = ((AnvilMenu) (Object) this).getCost();
        boolean canAfford = player.getAbilities().instabuild || ExperienceHelper.getTotalPoints(player) >= cost;
        cir.setReturnValue(canAfford && cost > 0);
    }

    @Inject(method = "onTake", at = @At("HEAD"), require = 0)
    private void not_enough_sigils$rememberPoints(Player player, ItemStack stack, CallbackInfo ci) {
        not_enough_sigils$cost = ((AnvilMenu) (Object) this).getCost();
        not_enough_sigils$pointsBefore = player.getAbilities().instabuild ? -1L : ExperienceHelper.getTotalPoints(player);
    }

    @Inject(method = "onTake", at = @At("RETURN"), require = 0)
    private void not_enough_sigils$chargePoints(Player player, ItemStack stack, CallbackInfo ci) {
        long before = not_enough_sigils$pointsBefore;
        not_enough_sigils$pointsBefore = -1L;
        if (before < 0L || player.level().isClientSide()) {
            return;
        }
        long target = Math.max(0L, before - not_enough_sigils$cost);
        if (ExperienceHelper.getTotalPoints(player) == target) {
            return;
        }
        player.experienceLevel = 0;
        player.experienceProgress = 0.0F;
        player.totalExperience = 0;
        player.giveExperiencePoints((int) Math.min(Integer.MAX_VALUE, target));
    }

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40), require = 0)
    private int not_enough_sigils$removeTooExpensive(int limit) {
        return Integer.MAX_VALUE;
    }
}
