package com.titammods.animus_extended.event;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.sigil.MonkSigilEnchantments;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

@EventBusSubscriber(modid = AnimusExtended.MODID)
public class SigilAnvilEvents {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (!MonkSigilEnchantments.isMonkSigil(left) || !right.is(Items.ENCHANTED_BOOK)) {
            return;
        }

        ItemEnchantments stored = right.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable result = new ItemEnchantments.Mutable(
                left.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));

        int cost = 0;
        boolean applied = false;

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            if (!MonkSigilEnchantments.isAllowed(holder) || !isCompatible(result, holder)) {
                continue;
            }

            int current = result.getLevel(holder);
            int level = mergeLevel(holder.value(), current, entry.getIntValue());
            if (level == current) {
                continue;
            }

            result.set(holder, level);
            cost += Math.max(1, holder.value().getAnvilCost() / 2) * level;
            applied = true;
        }

        if (!applied) {
            event.setCanceled(true);
            return;
        }

        ItemStack output = left.copy();
        output.set(DataComponents.ENCHANTMENTS, result.toImmutable());

        String name = event.getName();
        if (name != null && !name.isBlank() && !name.equals(left.getHoverName().getString())) {
            output.set(DataComponents.CUSTOM_NAME, Component.literal(name));
            cost += 1;
        }

        event.setOutput(output);
        event.setCost(Math.max(1, cost));
        event.setMaterialCost(1);
    }

    private static int mergeLevel(Enchantment enchantment, int current, int incoming) {
        int level;
        if (incoming > current) {
            level = incoming;
        } else if (incoming == current && current < enchantment.getMaxLevel()) {
            level = current + 1;
        } else {
            level = current;
        }
        return Math.min(MonkSigilEnchantments.MAX_LEVEL, level);
    }

    private static boolean isCompatible(ItemEnchantments.Mutable enchantments, Holder<Enchantment> candidate) {
        for (Holder<Enchantment> existing : enchantments.keySet()) {
            if (!existing.equals(candidate) && !Enchantment.areCompatible(candidate, existing)) {
                return false;
            }
        }
        return true;
    }
}
