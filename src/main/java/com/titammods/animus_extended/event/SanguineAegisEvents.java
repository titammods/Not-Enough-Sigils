package com.titammods.animus_extended.event;

import com.titammods.animus_extended.sigil.effect.SanguineAegisSigilEffect;
import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class SanguineAegisEvents {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        float damage = event.getNewDamage();
        if (damage <= 0.0F || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }
        ItemStack sigil = SigilTracker.getStack(player, SigilTracker.Kind.SANGUINE_AEGIS);
        if (sigil.isEmpty()) {
            return;
        }
        SanguineAegisSigilEffect config = SigilTracker.getEffect(player, SigilTracker.Kind.SANGUINE_AEGIS, SanguineAegisSigilEffect.class);
        int evPerHealth = Math.max(1, (config == null ? SanguineAegisSigilEffect.DEFAULT : config).evPerHealth());

        int available = SigilTracker.getEV(sigil);
        int cost = (int) Math.ceil(damage * evPerHealth);
        if (available >= cost) {
            SigilTracker.drainEV(player, sigil, cost);
            event.setNewDamage(0.0F);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.PLAYERS, 0.6F, 0.6F);
            return;
        }

        float absorbed = (float) available / evPerHealth;
        if (available > 0) {
            SigilTracker.drainEV(player, sigil, available);
        }
        event.setNewDamage(Math.max(0.0F, damage - absorbed));
        SigilTracker.deactivate(sigil);
        player.displayClientMessage(Component.translatable("message.animus_extended.sanguine_aegis.broken"), true);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
    }
}
