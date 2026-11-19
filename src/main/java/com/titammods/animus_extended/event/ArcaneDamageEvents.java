package com.titammods.animus_extended.event;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.setup.ModDamageTypes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class ArcaneDamageEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!event.getSource().is(ModDamageTypes.ARCANE)) {
            return;
        }
        event.setAmount(event.getOriginalAmount());
        event.addReductionModifier(DamageContainer.Reduction.ARMOR, (container, reduction) -> 0.0F);
        event.addReductionModifier(DamageContainer.Reduction.ENCHANTMENTS, (container, reduction) -> 0.0F);
        event.addReductionModifier(DamageContainer.Reduction.MOB_EFFECTS, (container, reduction) -> 0.0F);
        event.addReductionModifier(DamageContainer.Reduction.ABSORPTION, (container, reduction) -> 0.0F);
    }
}
