package com.titammods.animus_extended.event;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.MonkSigilEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.EnchantedEntityLootEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class SigilEnchantmentEvents {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack sigil = MonkSigilEnchantments.getEnchantedFistSigil(event.getEntity());
        if (sigil.isEmpty()) {
            return;
        }

        int level = MonkSigilEnchantments.getLevel(sigil, Enchantments.EFFICIENCY);
        if (level > 0) {
            event.setNewSpeed(event.getNewSpeed() + level * level + 1);
        }
    }

    @SubscribeEvent
    public static void onEnchantedEntityLoot(EnchantedEntityLootEvent event) {
        if (!(event.getDamageSource().getEntity() instanceof Player player)) {
            return;
        }
        if (event.getDamageSource().getDirectEntity() != player) {
            return;
        }

        ItemStack sigil = MonkSigilEnchantments.getEnchantedFistSigil(player);
        if (sigil.isEmpty()) {
            return;
        }

        int level = sigil.getEnchantmentLevel(event.getEnchantment());
        if (level > event.getEnchantmentLevel()) {
            event.setEnchantmentLevel(level);
        }
    }
}
