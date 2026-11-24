package com.titammods.not_enough_sigils.event;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.sigil.effect.ChainsSigilEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class ChainsEvents {
    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        if (!data.contains(ChainsSigilEffect.FALL_GRACE)) {
            return;
        }
        long until = data.getLong(ChainsSigilEffect.FALL_GRACE);
        data.remove(ChainsSigilEffect.FALL_GRACE);
        if (player.level().getGameTime() <= until) {
            event.setDamageMultiplier(0.0F);
        }
    }
}
