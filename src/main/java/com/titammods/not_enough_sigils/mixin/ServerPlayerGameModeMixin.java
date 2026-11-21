package com.titammods.not_enough_sigils.mixin;

import com.titammods.not_enough_sigils.sigil.MonkMode;
import com.titammods.not_enough_sigils.sigil.MonkSigilEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {

    @Shadow
    @Final
    protected ServerPlayer player;

    @ModifyArg(
            method = "destroyBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;playerDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V"
            ),
            index = 5
    )
    private ItemStack not_enough_sigils$useMonkSigilAsTool(ItemStack tool) {
        if (!tool.isEmpty()) {
            return tool;
        }

        ItemStack sigil = MonkSigilEnchantments.getEnchantedFistSigil(this.player);
        return sigil.isEmpty() || !MonkMode.of(sigil).mining() ? tool : sigil;
    }
}
