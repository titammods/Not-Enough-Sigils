package com.titammods.animus_extended.event;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.DemonNinjaState;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = AnimusExtended.MODID)
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
        if (!SigilTracker.isActive(player, SigilTracker.Kind.DEMON_NINJA)) {
            return;
        }
        DemonNinjaState.reveal(player);
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
