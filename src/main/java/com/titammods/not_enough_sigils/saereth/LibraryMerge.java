package com.titammods.not_enough_sigils.saereth;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.items.IItemHandler;

public class LibraryMerge {
    public record Match(int first, int second, ItemStack result, int levels) {
    }

    public static Match find(IItemHandler chest, int maxLevelOverride) {
        for (int i = 0; i < chest.getSlots(); i++) {
            ItemStack a = chest.getStackInSlot(i);
            ItemEnchantments enchantments = stored(a);
            if (enchantments == null) {
                continue;
            }
            Upgrade upgrade = upgrade(enchantments, maxLevelOverride);
            if (upgrade == null) {
                continue;
            }
            if (a.getCount() >= 2) {
                return new Match(i, i, upgrade.result(), upgrade.levels());
            }
            for (int j = i + 1; j < chest.getSlots(); j++) {
                if (enchantments.equals(stored(chest.getStackInSlot(j)))) {
                    return new Match(i, j, upgrade.result(), upgrade.levels());
                }
            }
        }
        return null;
    }

    private static ItemEnchantments stored(ItemStack stack) {
        if (!stack.is(Items.ENCHANTED_BOOK)) {
            return null;
        }
        ItemEnchantments enchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
        return enchantments == null || enchantments.isEmpty() ? null : enchantments;
    }

    private record Upgrade(ItemStack result, int levels) {
    }

    private static Upgrade upgrade(ItemEnchantments enchantments, int maxLevelOverride) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
        boolean changed = false;
        int levels = 0;
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            int level = entry.getIntValue();
            int cap = maxLevelOverride > 0 ? maxLevelOverride : entry.getKey().value().getMaxLevel();
            if (level < cap) {
                mutable.set(entry.getKey(), level + 1);
                levels += level + 1;
                changed = true;
            }
        }
        if (!changed) {
            return null;
        }
        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);
        result.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        return new Upgrade(result, levels);
    }
}
