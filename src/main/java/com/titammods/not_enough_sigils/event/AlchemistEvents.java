package com.titammods.not_enough_sigils.event;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModItems;
import com.titammods.not_enough_sigils.sigil.effect.AlchemistSigilEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class AlchemistEvents {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.SIGIL_ALCHEMIST.get())) {
            return;
        }
        boolean toExperience = AlchemistSigilEffect.isToExperience(stack);
        Component mode = Component.translatable("tooltip.not_enough_sigils.alchemist." + (toExperience ? "experience" : "essence"))
                .withStyle(toExperience ? ChatFormatting.GREEN : ChatFormatting.DARK_RED);
        event.getToolTip().add(Component.translatable("tooltip.not_enough_sigils.alchemist.mode", mode).withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable("tooltip.not_enough_sigils.alchemist.hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
