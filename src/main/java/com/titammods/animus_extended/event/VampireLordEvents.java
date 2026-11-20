package com.titammods.animus_extended.event;

import com.titammods.animus_extended.sigil.effect.VampireLordSigilEffect;
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
        VampireLordSigilEffect config = config(player);
        LivingEntity target = event.getEntity();
        float overkill = event.getNewDamage() - (target.getHealth() + target.getAbsorptionAmount());
        if (overkill > 0.0F) {
            SigilTracker.addEV(player, sigil, Math.min(config.maxEvPerKill(), Math.round(overkill * config.evPerOverkill())));
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
        player.heal(event.getNewDamage() * config(player).lifesteal());
    }

    private static VampireLordSigilEffect config(ServerPlayer player) {
        VampireLordSigilEffect config = SigilTracker.getEffect(player, SigilTracker.Kind.VAMPIRE_LORD, VampireLordSigilEffect.class);
        return config == null ? VampireLordSigilEffect.DEFAULT : config;
    }

    private static ServerPlayer meleeAttacker(Object attacker, Object direct, LivingEntity target) {
        if (attacker instanceof ServerPlayer player && direct == player && target != player) {
            return player;
        }
        return null;
    }
}
