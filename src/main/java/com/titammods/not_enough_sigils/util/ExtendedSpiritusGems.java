package com.titammods.not_enough_sigils.util;

import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.titammods.not_enough_sigils.setup.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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

    public static boolean isExtendedGem(Item item) {
        for (Gem gem : GEMS) {
            if (gem.item().get() == item) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack create(Item item, SpiritusType type, double amount) {
        ItemStack stack = new ItemStack(item);
        stack.set(NVDataComponents.SPIRITUS_TYPE, type);
        stack.set(NVDataComponents.SPIRITUS_AMOUNT, amount);
        return stack;
    }

    public static void addTabVariants(Consumer<ItemStack> output) {
        for (Gem gem : GEMS) {
            for (SpiritusType type : SpiritusType.values()) {
                output.accept(create(gem.item().get(), type, gem.capacity()));
            }
        }
        for (Gem gem : GEMS) {
            output.accept(create(gem.item().get(), SpiritusType.RAW, 0.0D));
        }
    }

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
