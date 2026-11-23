package com.titammods.not_enough_sigils.mixin;

import com.titammods.not_enough_sigils.util.ExperienceHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void not_enough_sigils$payWithPoints(Player player, boolean hasStack, CallbackInfoReturnable<Boolean> cir) {
        int cost = ((AnvilMenu) (Object) this).getCost();
        boolean canAfford = player.getAbilities().instabuild || ExperienceHelper.getTotalPoints(player) >= cost;
        cir.setReturnValue(canAfford && cost > 0);
    }

    @Redirect(
            method = "onTake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;giveExperienceLevels(I)V"
            )
    )
    private void not_enough_sigils$chargePoints(Player player, int levels) {
        player.giveExperiencePoints(levels);
    }

    @ModifyConstant(method = "createResultInternal", constant = @Constant(intValue = 40))
    private int not_enough_sigils$removeTooExpensive(int limit) {
        return Integer.MAX_VALUE;
    }
}
