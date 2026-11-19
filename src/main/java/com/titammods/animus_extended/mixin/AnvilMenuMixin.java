package com.titammods.animus_extended.mixin;

import com.titammods.animus_extended.util.ExperienceHelper;
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
    private void animus_extended$payWithPoints(Player player, boolean hasStack, CallbackInfoReturnable<Boolean> cir) {
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
    private void animus_extended$chargePoints(Player player, int levels) {
        player.giveExperiencePoints(levels);
    }

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40))
    private int animus_extended$removeTooExpensive(int limit) {
        return Integer.MAX_VALUE;
    }
}
