package com.titammods.not_enough_sigils.mixin;

import com.breakinblocks.animusnv.events.MonkSigilEventHandler;
import com.breakinblocks.animusnv.util.InventorySearchHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.titammods.not_enough_sigils.setup.ModItems;
import com.titammods.not_enough_sigils.sigil.MonkMode;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MonkSigilEventHandler.class, remap = false)
public abstract class MonkSigilEventHandlerMixin {
    private static final String HAS_ACTIVE = "Lcom/breakinblocks/animusnv/events/MonkSigilEventHandler;hasActiveMonkSigil(Lnet/minecraft/world/entity/player/Player;)Z";
    private static final String FIND_ACTIVE = "Lcom/breakinblocks/animusnv/events/MonkSigilEventHandler;findActiveMonkSigil(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/item/ItemStack;";

    @Inject(method = "findActiveMonkSigil", at = @At("RETURN"), cancellable = true)
    private static void not_enough_sigils$findSpiritualMonk(Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }
        ItemStack spiritual = InventorySearchHelper.findActiveSigil(player, ModItems.SIGIL_SPIRITUAL_MONK.get()).orElse(null);
        if (spiritual == null) {
            ItemStack tracked = SigilTracker.getStack(player, SigilTracker.Kind.SPIRITUAL_MONK);
            spiritual = tracked.isEmpty() ? null : tracked;
        }
        if (spiritual != null) {
            cir.setReturnValue(spiritual);
        }
    }

    @WrapOperation(method = {"onPlayerTick", "onLivingDeath"}, at = @At(value = "INVOKE", target = HAS_ACTIVE))
    private static boolean not_enough_sigils$combatOnly(Player player, Operation<Boolean> original) {
        return original.call(player) && MonkMode.allowsCombat(player);
    }

    @WrapOperation(method = {"onBreakSpeed", "onHarvestCheck"}, at = @At(value = "INVOKE", target = HAS_ACTIVE))
    private static boolean not_enough_sigils$miningOnly(Player player, Operation<Boolean> original) {
        return original.call(player) && MonkMode.allowsMining(player);
    }

    @WrapOperation(method = "onLivingHurt", at = @At(value = "INVOKE", target = FIND_ACTIVE))
    private static ItemStack not_enough_sigils$combatSigil(Player player, Operation<ItemStack> original) {
        ItemStack sigil = original.call(player);
        return sigil != null && MonkMode.of(sigil).combat() ? sigil : null;
    }

    @WrapOperation(method = "onBlockBreak", at = @At(value = "INVOKE", target = FIND_ACTIVE))
    private static ItemStack not_enough_sigils$miningSigil(Player player, Operation<ItemStack> original) {
        ItemStack sigil = original.call(player);
        return sigil != null && MonkMode.of(sigil).mining() ? sigil : null;
    }
}
