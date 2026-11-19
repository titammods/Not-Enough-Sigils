package com.titammods.animus_extended.setup;

import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.holding.GreaterHoldingMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, AnimusExtended.MODID);

    public static final Supplier<MenuType<GreaterHoldingMenu>> GREATER_HOLDING = MENUS.register("sigil_holding_greater",
            () -> IMenuTypeExtension.create(GreaterHoldingMenu::new));
}
