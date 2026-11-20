package com.titammods.not_enough_sigils.event;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.sigil.DemonNinjaState;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import com.titammods.not_enough_sigils.sigil.effect.DemonNinjaSigilEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class DemonNinjaEvents {

    @SubscribeEvent
    public static void onDamageTaken(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        if (event.getNewDamage() <= 0.0F) {
            return;
        }
        Entity attacker = event.getSource().getEntity();
        if (attacker == null || attacker == player) {
            return;
        }
        DemonNinjaSigilEffect config = SigilTracker.getEffect(player, SigilTracker.Kind.DEMON_NINJA, DemonNinjaSigilEffect.class);
        if (config == null) {
            return;
        }
        DemonNinjaState.reveal(player, config.revealTicks());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        DemonNinjaState.clearIfInactive(player);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        DemonNinjaState.clear(event.getEntity().getUUID());
        SigilTracker.clear(event.getEntity().getUUID());
    }
}
