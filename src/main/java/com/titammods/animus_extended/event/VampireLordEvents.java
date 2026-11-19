package com.titammods.animus_extended.event;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class VampireLordEvents {
    private static final float LIFESTEAL_RATIO = 0.15F;
    private static final int EV_PER_OVERKILL = 50;
    private static final int MAX_EV_PER_KILL = 5000;

    @SubscribeEvent
    public static void onDamagePre(LivingDamageEvent.Pre event) {
        ServerPlayer player = meleeAttacker(event.getSource().getEntity(), event.getSource().getDirectEntity(), event.getEntity());
        if (player == null || event.getNewDamage() <= 0.0F) {
            return;
        }
        ItemStack sigil = SigilTracker.getStack(player, SigilTracker.Kind.VAMPIRE_LORD);
        if (sigil.isEmpty()) {
            return;
        }
        LivingEntity target = event.getEntity();
        float overkill = event.getNewDamage() - (target.getHealth() + target.getAbsorptionAmount());
        if (overkill > 0.0F) {
            SigilTracker.addEV(player, sigil, Math.min(MAX_EV_PER_KILL, Math.round(overkill * EV_PER_OVERKILL)));
        }
    }

    @SubscribeEvent
    public static void onDamagePost(LivingDamageEvent.Post event) {
        ServerPlayer player = meleeAttacker(event.getSource().getEntity(), event.getSource().getDirectEntity(), event.getEntity());
        if (player == null || event.getNewDamage() <= 0.0F) {
            return;
        }
        if (!SigilTracker.isActive(player, SigilTracker.Kind.VAMPIRE_LORD)) {
            return;
        }
        player.heal(event.getNewDamage() * LIFESTEAL_RATIO);
    }

    private static ServerPlayer meleeAttacker(Object attacker, Object direct, LivingEntity target) {
        if (attacker instanceof ServerPlayer player && direct == player && target != player) {
            return player;
        }
        return null;
    }
}
