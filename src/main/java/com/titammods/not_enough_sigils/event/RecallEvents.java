package com.titammods.not_enough_sigils.event;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModItems;
import com.titammods.not_enough_sigils.sigil.effect.RecallSigilEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class RecallEvents {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.SIGIL_RECALL.get()) && !stack.is(ModItems.SIGIL_RECALL_GREATER.get())) {
            return;
        }
        RecallSigilEffect.Mark mark = RecallSigilEffect.Mark.get(stack);
        if (mark == null) {
            event.getToolTip().add(Component.translatable("tooltip.not_enough_sigils.recall.unmarked").withStyle(ChatFormatting.GRAY));
        } else {
            Vec3 pos = mark.position();
            event.getToolTip().add(Component.translatable("tooltip.not_enough_sigils.recall.mark",
                    (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z),
                    mark.dimension().location().toString()).withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        event.getToolTip().add(Component.translatable("tooltip.not_enough_sigils.recall.hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
