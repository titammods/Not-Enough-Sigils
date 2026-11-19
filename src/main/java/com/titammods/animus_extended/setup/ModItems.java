package com.titammods.animus_extended.setup;

import com.breakinblocks.neovitae.common.item.sigil.SigilItem;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import com.titammods.animus_extended.AnimusExtended;
import com.titammods.animus_extended.holding.GreaterHoldingSigilItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AnimusExtended.MODID);

    public static final DeferredItem<SigilItem> SIGIL_SOUL_PHOENIX = registerSigil("sigil_soul_phoenix", "soul_phoenix");
    public static final DeferredItem<SigilItem> SIGIL_DEMON_NINJA = registerSigil("sigil_demon_ninja", "demon_ninja");
    public static final DeferredItem<SigilItem> SIGIL_BERSERK = registerSigil("sigil_berserk", "berserk");
    public static final DeferredItem<SigilItem> SIGIL_MAGICIAN = registerSigil("sigil_magician", "magician");
    public static final DeferredItem<SigilItem> SIGIL_BRUNIMNEETS = registerSigil("sigil_brunimneets", "brunimneets");
    public static final DeferredItem<SigilItem> SIGIL_SANGUINE_AEGIS = registerSigil("sigil_sanguine_aegis", "sanguine_aegis");
    public static final DeferredItem<SigilItem> SIGIL_VAMPIRE_LORD = registerSigil("sigil_vampire_lord", "vampire_lord");
    public static final DeferredItem<SigilItem> SIGIL_ETHEREAL_STEP = registerSigil("sigil_ethereal_step", "ethereal_step");
    public static final DeferredItem<SigilItem> SIGIL_SKYWARD_SOUL = registerSigil("sigil_skyward_soul", "skyward_soul");
    public static final DeferredItem<SigilItem> SIGIL_REAPER = registerSigil("sigil_reaper", "reaper");
    public static final DeferredItem<SigilItem> SIGIL_HELLBRAND = registerSigil("sigil_hellbrand", "hellbrand");
    public static final DeferredItem<SigilItem> SIGIL_VEIN_SEEKER = registerSigil("sigil_vein_seeker", "vein_seeker");
    public static final DeferredItem<SigilItem> SIGIL_WARDING_GALE = registerSigil("sigil_warding_gale", "warding_gale");
    public static final DeferredItem<SigilItem> SIGIL_BULLET_TIME = registerSigil("sigil_bullet_time", "bullet_time");
    public static final DeferredItem<SigilItem> SIGIL_SOUL_PHOENIX_GREATER = registerSigil("sigil_soul_phoenix_greater", "soul_phoenix_greater");
    public static final DeferredItem<SigilItem> SIGIL_ARCHMAGE = registerSigil("sigil_archmage", "archmage");
    public static final DeferredItem<SigilItem> SIGIL_SPIRITUAL_MONK = registerSigil("sigil_spiritual_monk", "spiritual_monk");
    public static final DeferredItem<SigilItem> SIGIL_PONYMEAL = registerSigil("sigil_ponymeal", "ponymeal");

    public static final DeferredItem<GreaterHoldingSigilItem> SIGIL_HOLDING_GREATER =
            ITEMS.register("sigil_holding_greater", () -> new GreaterHoldingSigilItem(9, "tooltip.animus_extended.sigil_holding_greater.desc"));
    public static final DeferredItem<GreaterHoldingSigilItem> SIGIL_HOLDING_SUPREME =
            ITEMS.register("sigil_holding_supreme", () -> new GreaterHoldingSigilItem(18, "tooltip.animus_extended.sigil_holding_supreme.desc"));

    public static final DeferredItem<Item> REAGENT_SOUL_PHOENIX = ITEMS.registerSimpleItem("reagent_soul_phoenix");
    public static final DeferredItem<Item> REAGENT_DEMON_NINJA = ITEMS.registerSimpleItem("reagent_demon_ninja");
    public static final DeferredItem<Item> REAGENT_BERSERK = ITEMS.registerSimpleItem("reagent_berserk");
    public static final DeferredItem<Item> REAGENT_MAGICIAN = ITEMS.registerSimpleItem("reagent_magician");
    public static final DeferredItem<Item> REAGENT_BRUNIMNEETS = ITEMS.registerSimpleItem("reagent_brunimneets");
    public static final DeferredItem<Item> REAGENT_SANGUINE_AEGIS = ITEMS.registerSimpleItem("reagent_sanguine_aegis");
    public static final DeferredItem<Item> REAGENT_VAMPIRE_LORD = ITEMS.registerSimpleItem("reagent_vampire_lord");
    public static final DeferredItem<Item> REAGENT_ETHEREAL_STEP = ITEMS.registerSimpleItem("reagent_ethereal_step");
    public static final DeferredItem<Item> REAGENT_SKYWARD_SOUL = ITEMS.registerSimpleItem("reagent_skyward_soul");
    public static final DeferredItem<Item> REAGENT_REAPER = ITEMS.registerSimpleItem("reagent_reaper");
    public static final DeferredItem<Item> REAGENT_HELLBRAND = ITEMS.registerSimpleItem("reagent_hellbrand");
    public static final DeferredItem<Item> REAGENT_VEIN_SEEKER = ITEMS.registerSimpleItem("reagent_vein_seeker");
    public static final DeferredItem<Item> REAGENT_WARDING_GALE = ITEMS.registerSimpleItem("reagent_warding_gale");
    public static final DeferredItem<Item> REAGENT_BULLET_TIME = ITEMS.registerSimpleItem("reagent_bullet_time");

    public static final DeferredItem<Item> REAGENT_PONYMEAL = ITEMS.registerSimpleItem("reagent_ponymeal");

    public static final DeferredItem<Item> NINJA_KNIFE = ITEMS.registerSimpleItem("ninja_knife");
    public static final DeferredItem<Item> NINJA_KNIFE_EXPLOSIVE = ITEMS.registerSimpleItem("ninja_knife_explosive");
    public static final DeferredItem<Item> NINJA_KNIFE_LIGHTNING = ITEMS.registerSimpleItem("ninja_knife_lightning");
    public static final DeferredItem<Item> NINJA_KNIFE_FROST = ITEMS.registerSimpleItem("ninja_knife_frost");
    public static final DeferredItem<Item> NINJA_KNIFE_FIRE = ITEMS.registerSimpleItem("ninja_knife_fire");

    public static boolean isHiddenFromTab(Item item) {
        return item == NINJA_KNIFE.get() || item == NINJA_KNIFE_EXPLOSIVE.get() || item == NINJA_KNIFE_LIGHTNING.get()
                || item == NINJA_KNIFE_FROST.get() || item == NINJA_KNIFE_FIRE.get();
    }

    private static DeferredItem<SigilItem> registerSigil(String name, String sigilType) {
        return ITEMS.register(name, () -> new SigilItem(SigilTypeRegistry.key(AnimusExtended.rl(sigilType))));
    }
}
