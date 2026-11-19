package com.titammods.animus_extended.mixin;

import com.titammods.animus_extended.sigil.MonkSigilEnchantments;
import com.titammods.animus_extended.sigil.PhoenixGuard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "getWeaponItem", at = @At("RETURN"), cancellable = true)
    private void animus_extended$useMonkSigilAsWeapon(CallbackInfoReturnable<ItemStack> cir) {
        if (!((Object) this instanceof Player player)) {
            return;
        }
        if (!cir.getReturnValue().isEmpty()) {
            return;
        }

        ItemStack sigil = MonkSigilEnchantments.getEnchantedFistSigil(player);
        if (!sigil.isEmpty()) {
            cir.setReturnValue(sigil);
        }
    }

    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    private void animus_extended$phoenixGuard(float health, CallbackInfo ci) {
        if (!((Object) this instanceof ServerPlayer player) || PhoenixGuard.isReviving()) {
            return;
        }
        float current = player.getHealth();
        if (current <= 0.0F) {
            return;
        }
        boolean lethal = Float.isNaN(health) || health <= 0.0F;
        if (PhoenixGuard.inGrace(player) && (lethal || health < current)) {
            ci.cancel();
            return;
        }
        if (lethal && PhoenixGuard.tryGreaterRevive(player, false)) {
            ci.cancel();
        }
    }
}
