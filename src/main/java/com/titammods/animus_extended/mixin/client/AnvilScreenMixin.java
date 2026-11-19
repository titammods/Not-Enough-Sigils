package com.titammods.animus_extended.mixin.client;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {

    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 40))
    private int animus_extended$removeTooExpensive(int limit) {
        return Integer.MAX_VALUE;
    }
}
