package com.titammods.animus_extended.mixin;

import com.breakinblocks.animusnv.events.MonkSigilEventHandler;
import com.breakinblocks.animusnv.util.InventorySearchHelper;
import com.titammods.animus_extended.setup.ModItems;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MonkSigilEventHandler.class, remap = false)
public abstract class MonkSigilEventHandlerMixin {

    @Inject(method = "findActiveMonkSigil", at = @At("RETURN"), cancellable = true)
    private static void animus_extended$findSpiritualMonk(Player player, CallbackInfoReturnable<ItemStack> cir) {
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
}
