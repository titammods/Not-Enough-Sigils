package com.titammods.animus_extended.util;

import com.titammods.animus_extended.setup.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ExtendedSpiritusGems {
    public static final double EXALTED_CAPACITY = 32768.0D;
    public static final double PRIMORDIAL_CAPACITY = 65536.0D;

    private record Gem(Supplier<? extends Item> item, double capacity) {
    }

    private static final List<Gem> GEMS = List.of(
            new Gem(ModItems.SPIRITUS_GEM_EXALTED, EXALTED_CAPACITY),
            new Gem(ModItems.SPIRITUS_GEM_PRIMORDIAL, PRIMORDIAL_CAPACITY)
    );

    public static List<ItemStack> withExtended(List<ItemStack> validGems, double minSpiritus) {
        List<ItemStack> result = new ArrayList<>(validGems);
        for (Gem gem : GEMS) {
            if (gem.capacity() >= minSpiritus) {
                result.add(new ItemStack(gem.item().get()));
            }
        }
        return result;
    }
}
