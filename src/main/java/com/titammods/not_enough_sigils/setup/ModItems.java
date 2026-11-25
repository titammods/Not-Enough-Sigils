package com.titammods.not_enough_sigils.setup;

import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.item.SpiritusGemItem;
import com.breakinblocks.neovitae.common.item.block.RuneBlockItem;
import com.breakinblocks.neovitae.common.item.sigil.SigilItem;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.book.GuideBookItem;
import com.titammods.not_enough_sigils.holding.GreaterHoldingSigilItem;
import com.titammods.not_enough_sigils.item.ProgressionItem;
import com.titammods.not_enough_sigils.saereth.SoulCaptureSigilItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NotEnoughSigils.MODID);

    public static final DeferredItem<GuideBookItem> GUIDE_BOOK = ITEMS.register("guide_book", GuideBookItem::new);

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
    public static final DeferredItem<SigilItem> SIGIL_TEMPORAL_MASTERY = registerSigil("sigil_temporal_mastery", "temporal_mastery");
    public static final DeferredItem<SigilItem> SIGIL_CURSE_OF_GLUTTONY = registerSigil("sigil_curse_of_gluttony", "curse_of_gluttony");
    public static final DeferredItem<SigilItem> SIGIL_FAIRYS_BLESSING_DIVINE = registerSigil("sigil_fairys_blessing_divine", "fairys_blessing_divine");
    public static final DeferredItem<SigilItem> SIGIL_BERSERK_DIVINE = registerSigil("sigil_berserk_divine", "berserk_divine");
    public static final DeferredItem<SigilItem> SIGIL_FAIRYS_BLESSING = registerSigil("sigil_fairys_blessing", "fairys_blessing");
    public static final DeferredItem<SigilItem> SIGIL_RECALL = registerSigil("sigil_recall", "recall");
    public static final DeferredItem<SigilItem> SIGIL_ALCHEMIST = registerSigil("sigil_alchemist", "alchemist");
    public static final DeferredItem<SigilItem> SIGIL_SANCTUARY = registerSigil("sigil_sanctuary", "sanctuary");
    public static final DeferredItem<SigilItem> SIGIL_EXECUTIONER = registerSigil("sigil_executioner", "executioner");
    public static final DeferredItem<SigilItem> SIGIL_CHAINS = registerSigil("sigil_chains", "chains");
    public static final DeferredItem<SigilItem> SIGIL_HARVEST = registerSigil("sigil_harvest", "harvest");
    public static final DeferredItem<SigilItem> SIGIL_QUICKSILVER = registerSigil("sigil_quicksilver", "quicksilver");
    public static final DeferredItem<SigilItem> SIGIL_ETHEREAL_STEP_GREATER = registerSigil("sigil_ethereal_step_greater", "ethereal_step_greater");
    public static final DeferredItem<SigilItem> SIGIL_VEIN_SEEKER_GREATER = registerSigil("sigil_vein_seeker_greater", "vein_seeker_greater");
    public static final DeferredItem<SigilItem> SIGIL_BULLET_TIME_GREATER = registerSigil("sigil_bullet_time_greater", "bullet_time_greater");
    public static final DeferredItem<SigilItem> SIGIL_WARDING_GALE_GREATER = registerSigil("sigil_warding_gale_greater", "warding_gale_greater");
    public static final DeferredItem<SigilItem> SIGIL_SANGUINE_AEGIS_GREATER = registerSigil("sigil_sanguine_aegis_greater", "sanguine_aegis_greater");
    public static final DeferredItem<SigilItem> SIGIL_VAMPIRE_LORD_GREATER = registerSigil("sigil_vampire_lord_greater", "vampire_lord_greater");
    public static final DeferredItem<SigilItem> SIGIL_HELLBRAND_GREATER = registerSigil("sigil_hellbrand_greater", "hellbrand_greater");
    public static final DeferredItem<SigilItem> SIGIL_REAPER_GREATER = registerSigil("sigil_reaper_greater", "reaper_greater");
    public static final DeferredItem<SigilItem> SIGIL_SKYWARD_SOUL_GREATER = registerSigil("sigil_skyward_soul_greater", "skyward_soul_greater");
    public static final DeferredItem<SigilItem> SIGIL_DEMON_NINJA_GREATER = registerSigil("sigil_demon_ninja_greater", "demon_ninja_greater");
    public static final DeferredItem<SigilItem> SIGIL_RECALL_GREATER = registerSigil("sigil_recall_greater", "recall_greater");
    public static final DeferredItem<BloodOrbItem> BLOOD_ORB_ASCENDANT = ITEMS.register("blood_orb_ascendant", BloodOrbItem::new);
    public static final DeferredItem<BloodOrbItem> BLOOD_ORB_DIVINE = ITEMS.register("blood_orb_divine", BloodOrbItem::new);
    public static final DeferredItem<SoulCaptureSigilItem> SIGIL_SOUL_CAPTURE = ITEMS.register("sigil_soul_capture", SoulCaptureSigilItem::new);

    public static final DeferredItem<GreaterHoldingSigilItem> SIGIL_HOLDING_GREATER =
            ITEMS.register("sigil_holding_greater", () -> new GreaterHoldingSigilItem(9, "tooltip.not_enough_sigils.sigil_holding_greater.desc"));
    public static final DeferredItem<GreaterHoldingSigilItem> SIGIL_HOLDING_SUPREME =
            ITEMS.register("sigil_holding_supreme", () -> new GreaterHoldingSigilItem(18, "tooltip.not_enough_sigils.sigil_holding_supreme.desc"));
    public static final DeferredItem<GreaterHoldingSigilItem> SIGIL_HOLDING_DIVINE =
            ITEMS.register("sigil_holding_divine", () -> new GreaterHoldingSigilItem(27, "tooltip.not_enough_sigils.sigil_holding_divine.desc"));

    public static final DeferredItem<RuneBlockItem> RUNE_3_CAPACITY_AUGMENTED = rune(ModBlocks.RUNE_3_CAPACITY_AUGMENTED, "rune.capacity_augmented", "rune.ascended");
    public static final DeferredItem<RuneBlockItem> RUNE_3_SPEED = rune(ModBlocks.RUNE_3_SPEED, "rune.speed", "rune.ascended");
    public static final DeferredItem<RuneBlockItem> RUNE_ETERNITY = rune(ModBlocks.RUNE_ETERNITY, "rune.eternity", "rune.ascended");
    public static final DeferredItem<RuneBlockItem> RUNE_3_SACRIFICE = rune(ModBlocks.RUNE_3_SACRIFICE, "rune.sacrifice", "rune.ascended");
    public static final DeferredItem<RuneBlockItem> RUNE_3_SACRIFICE_SELF = rune(ModBlocks.RUNE_3_SACRIFICE_SELF, "rune.sacrifice_self", "rune.ascended");
    public static final DeferredItem<RuneBlockItem> RUNE_3_ORB = rune(ModBlocks.RUNE_3_ORB, "rune.orb", "rune.ascended");
    public static final DeferredItem<RuneBlockItem> RUNE_3_DISLOCATION = rune(ModBlocks.RUNE_3_DISLOCATION, "rune.dislocation", "rune.ascended");
    public static final DeferredItem<RuneBlockItem> RUNE_3_CHARGING = rune(ModBlocks.RUNE_3_CHARGING, "rune.charging", "rune.ascended");

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
    public static final DeferredItem<Item> REAGENT_CURSE_OF_GLUTTONY = ITEMS.registerSimpleItem("reagent_curse_of_gluttony");
    public static final DeferredItem<Item> REAGENT_FAIRYS_BLESSING = ITEMS.registerSimpleItem("reagent_fairys_blessing");
    public static final DeferredItem<Item> REAGENT_RECALL = ITEMS.registerSimpleItem("reagent_recall");
    public static final DeferredItem<Item> REAGENT_ALCHEMIST = ITEMS.registerSimpleItem("reagent_alchemist");
    public static final DeferredItem<Item> REAGENT_SANCTUARY = ITEMS.registerSimpleItem("reagent_sanctuary");
    public static final DeferredItem<Item> REAGENT_EXECUTIONER = ITEMS.registerSimpleItem("reagent_executioner");
    public static final DeferredItem<Item> REAGENT_CHAINS = ITEMS.registerSimpleItem("reagent_chains");
    public static final DeferredItem<Item> REAGENT_HARVEST = ITEMS.registerSimpleItem("reagent_harvest");
    public static final DeferredItem<Item> REAGENT_QUICKSILVER = ITEMS.registerSimpleItem("reagent_quicksilver");

    public static final DeferredItem<SpiritusGemItem> SPIRITUS_GEM_EXALTED = ITEMS.register("spiritus_gem_exalted", SpiritusGemItem::new);
    public static final DeferredItem<SpiritusGemItem> SPIRITUS_GEM_PRIMORDIAL = ITEMS.register("spiritus_gem_primordial", SpiritusGemItem::new);

    public static final DeferredItem<ProgressionItem> SOUL_FRAGMENT = material("soul_fragment", Rarity.UNCOMMON);
    public static final DeferredItem<ProgressionItem> ASCENDED_ESSENCE = material("ascended_essence", Rarity.RARE);
    public static final DeferredItem<ProgressionItem> DIVINE_ESSENCE = material("divine_essence", Rarity.EPIC);

    public static final DeferredItem<Item> NINJA_KNIFE = ITEMS.registerSimpleItem("ninja_knife");
    public static final DeferredItem<Item> NINJA_KNIFE_EXPLOSIVE = ITEMS.registerSimpleItem("ninja_knife_explosive");
    public static final DeferredItem<Item> NINJA_KNIFE_LIGHTNING = ITEMS.registerSimpleItem("ninja_knife_lightning");
    public static final DeferredItem<Item> NINJA_KNIFE_FROST = ITEMS.registerSimpleItem("ninja_knife_frost");
    public static final DeferredItem<Item> NINJA_KNIFE_FIRE = ITEMS.registerSimpleItem("ninja_knife_fire");

    public static boolean isHiddenFromTab(Item item) {
        return item == NINJA_KNIFE.get() || item == NINJA_KNIFE_EXPLOSIVE.get() || item == NINJA_KNIFE_LIGHTNING.get()
                || item == NINJA_KNIFE_FROST.get() || item == NINJA_KNIFE_FIRE.get();
    }

    private static DeferredItem<ProgressionItem> material(String name, Rarity rarity) {
        return ITEMS.register(name, () -> new ProgressionItem(new Item.Properties().rarity(rarity), "tooltip." + NotEnoughSigils.MODID + "." + name));
    }

    private static DeferredItem<RuneBlockItem> rune(DeferredBlock<Block> block, String... tooltips) {
        String[] keys = new String[tooltips.length];
        for (int i = 0; i < tooltips.length; i++) {
            keys[i] = "tooltip." + NotEnoughSigils.MODID + "." + tooltips[i];
        }
        return ITEMS.register(block.getId().getPath(), () -> new RuneBlockItem(block.get(), new Item.Properties().rarity(Rarity.EPIC), keys));
    }

    private static DeferredItem<SigilItem> registerSigil(String name, String sigilType) {
        return ITEMS.register(name, () -> new SigilItem(SigilTypeRegistry.key(NotEnoughSigils.rl(sigilType))));
    }

    public static Item[] bloodOrbs() {
        return new Item[]{BLOOD_ORB_ASCENDANT.get(), BLOOD_ORB_DIVINE.get()};
    }
}
