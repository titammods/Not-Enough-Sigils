package com.titammods.animus_extended.event;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.BulletTimeState;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class BulletTimeEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        BulletTimeState.tick(player);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        BulletTimeState.clear(event.getEntity().getUUID());
    }
}
