package com.titammods.not_enough_sigils.event;

import com.titammods.not_enough_sigils.sigil.effect.VampireLordSigilEffect;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = NotEnoughSigils.MODID)
public class VampireLordEvents {
    private static final ResourceLocation OVERHEAL = NotEnoughSigils.rl("vampire_overheal");

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
        VampireLordSigilEffect config = config(player);
        float amount = event.getNewDamage() * config.lifesteal();
        float missing = player.getMaxHealth() - player.getHealth();
        player.heal(amount);
        float overflow = amount - missing;
        if (config.overhealCap() > 0.0F && overflow > 0.0F) {
            updateOverheal(player, config.overhealCap());
            player.setAbsorptionAmount(Math.min(config.overhealCap(), player.getAbsorptionAmount() + overflow));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 20 != 0) {
            return;
        }
        VampireLordSigilEffect config = SigilTracker.getEffect(player, SigilTracker.Kind.VAMPIRE_LORD, VampireLordSigilEffect.class);
        updateOverheal(player, config == null ? 0.0F : config.overhealCap());
    }

    private static void updateOverheal(ServerPlayer player, float cap) {
        AttributeInstance attribute = player.getAttribute(Attributes.MAX_ABSORPTION);
        if (attribute == null) {
            return;
        }
        if (cap > 0.0F) {
            attribute.addOrUpdateTransientModifier(new AttributeModifier(OVERHEAL, cap, AttributeModifier.Operation.ADD_VALUE));
        } else if (attribute.hasModifier(OVERHEAL)) {
            attribute.removeModifier(OVERHEAL);
            player.setAbsorptionAmount(player.getAbsorptionAmount());
        }
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
