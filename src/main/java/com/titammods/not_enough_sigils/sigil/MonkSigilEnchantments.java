package com.titammods.not_enough_sigils.sigil;

import com.breakinblocks.animusnv.events.MonkSigilEventHandler;
import com.breakinblocks.animusnv.registry.AnimusItems;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModItems;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

public class MonkSigilEnchantments {
    public static final int MAX_LEVEL = 255;

    public static final TagKey<Enchantment> BLACKLIST =
            TagKey.create(Registries.ENCHANTMENT, NotEnoughSigils.rl("monk_sigil_blacklist"));

    private static List<ItemStack> referenceTools;

    private MonkSigilEnchantments() {
    }

    public static boolean isMonkSigil(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(AnimusItems.SIGIL_MONK.get()) || stack.is(ModItems.SIGIL_SPIRITUAL_MONK.get()));
    }

    public static boolean isAllowed(Holder<Enchantment> enchantment) {
        if (enchantment.is(BLACKLIST)) {
            return false;
        }
        for (ItemStack tool : referenceTools()) {
            if (tool.supportsEnchantment(enchantment)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack getEnchantedFistSigil(Player player) {
        if (!player.getMainHandItem().isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack sigil = MonkSigilEventHandler.findActiveMonkSigil(player);
        if (sigil == null || sigil.isEmpty() || !sigil.isEnchanted()) {
            return ItemStack.EMPTY;
        }
        return sigil;
    }

    public static int getLevel(ItemStack stack, ResourceKey<Enchantment> key) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    private static List<ItemStack> referenceTools() {
        if (referenceTools == null) {
            referenceTools = List.of(
                    new ItemStack(Items.NETHERITE_PICKAXE),
                    new ItemStack(Items.NETHERITE_SHOVEL),
                    new ItemStack(Items.NETHERITE_AXE),
                    new ItemStack(Items.NETHERITE_SWORD)
            );
        }
        return referenceTools;
    }
}
