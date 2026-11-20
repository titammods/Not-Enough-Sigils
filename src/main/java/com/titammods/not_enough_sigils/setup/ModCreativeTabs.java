package com.titammods.not_enough_sigils.setup;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.util.ExtendedSpiritusGems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NotEnoughSigils.MODID);

    public static final Supplier<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.not_enough_sigils"))
            .icon(() -> new ItemStack(ModItems.SIGIL_SOUL_PHOENIX.get()))
            .displayItems((parameters, output) -> {
                ModItems.ITEMS.getEntries().stream()
                        .map(entry -> entry.get())
                        .filter(item -> !ModItems.isHiddenFromTab(item) && !ExtendedSpiritusGems.isExtendedGem(item))
                        .forEach(output::accept);
                ExtendedSpiritusGems.addTabVariants(output::accept);
            })
            .build());
}
