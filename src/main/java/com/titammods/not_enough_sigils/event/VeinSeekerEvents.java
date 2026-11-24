package com.titammods.not_enough_sigils.event;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModItems;
import com.titammods.not_enough_sigils.sigil.effect.VeinSeekerSigilEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class VeinSeekerEvents {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.SIGIL_VEIN_SEEKER_GREATER.get())) {
            return;
        }
        Block filter = VeinSeekerSigilEffect.filter(stack);
        Component value = filter == null
                ? Component.translatable("tooltip.not_enough_sigils.vein_seeker.all_ores")
                : filter.getName();
        event.getToolTip().add(Component.translatable("tooltip.not_enough_sigils.vein_seeker.filter", value.copy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable("tooltip.not_enough_sigils.vein_seeker.filter_hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
