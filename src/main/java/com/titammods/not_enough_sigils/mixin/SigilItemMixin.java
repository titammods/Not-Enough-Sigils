package com.titammods.not_enough_sigils.mixin;

import com.breakinblocks.neovitae.common.item.sigil.SigilItem;
import com.titammods.not_enough_sigils.sigil.MonkSigilEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SigilItem.class, remap = false)
public abstract class SigilItemMixin extends Item {
    private SigilItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (MonkSigilEnchantments.isMonkSigil(stack)) {
            return MonkSigilEnchantments.isAllowed(enchantment);
        }
        return super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        if (MonkSigilEnchantments.isMonkSigil(stack)) {
            return MonkSigilEnchantments.isAllowed(enchantment);
        }
        return super.isPrimaryItemFor(stack, enchantment);
    }
}
