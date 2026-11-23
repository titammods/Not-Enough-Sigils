package com.titammods.not_enough_sigils.datagen;

import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.breakinblocks.neovitae.common.recipe.aravitae.AraVitaeRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeTransformRecipe;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        forgeRecipes(output);
        arrayRecipes(output);
        altarRecipes(output);
    }

    private void forgeRecipes(RecipeOutput output) {
        forge(output, ModItems.REAGENT_SOUL_PHOENIX.get(), 2048, 4096, Items.TOTEM_OF_UNDYING, Items.NETHER_STAR, Items.BLAZE_ROD, nv("animus_mote"));
        forge(output, ModItems.REAGENT_DEMON_NINJA.get(), 64, 128, Items.FERMENTED_SPIDER_EYE, Items.GOLDEN_CARROT, Items.PHANTOM_MEMBRANE, Items.BLACK_WOOL);
        forge(output, ModItems.REAGENT_BERSERK.get(), 1000, 2048, Items.BLAZE_ROD, Items.GOLDEN_APPLE, Items.DIAMOND_AXE, Items.REDSTONE_BLOCK);
        forge(output, ModItems.REAGENT_MAGICIAN.get(), 256, 512, Items.AMETHYST_BLOCK, Items.ENDER_EYE, Items.CHORUS_FRUIT, Items.BOOK);
        forge(output, ModItems.REAGENT_BRUNIMNEETS.get(), 256, 512, Items.SPYGLASS, Items.ECHO_SHARD, Items.ENDER_PEARL, Items.PUFFERFISH);
        forge(output, ModItems.REAGENT_SANGUINE_AEGIS.get(), 5000, 5000, Items.NETHERITE_BLOCK, Items.CONDUIT, nv("deus_shard"), nv("spiritus_gem_grand"));
        forge(output, ModItems.REAGENT_VAMPIRE_LORD.get(), 256, 512, Items.WITHER_SKELETON_SKULL, Items.GLISTERING_MELON_SLICE, Items.GHAST_TEAR, Items.IRON_SWORD);
        forge(output, ModItems.REAGENT_ETHEREAL_STEP.get(), 64, 128, Items.ENDER_PEARL, Items.CHORUS_FRUIT, Items.FEATHER, Items.PHANTOM_MEMBRANE);
        forge(output, ModItems.REAGENT_SKYWARD_SOUL.get(), 512, 1024, Items.ELYTRA, nv("sigil_air"), Items.PHANTOM_MEMBRANE, Items.GHAST_TEAR);
        forge(output, ModItems.REAGENT_REAPER.get(), 256, 512, Items.WITHER_SKELETON_SKULL, Items.SOUL_SAND, Items.DIAMOND_HOE, Items.BONE_BLOCK);
        forge(output, ModItems.REAGENT_HELLBRAND.get(), 256, 512, Items.BLAZE_ROD, Items.FIRE_CHARGE, Items.MAGMA_BLOCK, Items.NETHERITE_SCRAP);
        forge(output, ModItems.REAGENT_VEIN_SEEKER.get(), 64, 128, Items.COMPASS, Items.DIAMOND, Items.GLOWSTONE, Items.EMERALD);
        forge(output, ModItems.REAGENT_WARDING_GALE.get(), 128, 256, Items.BREEZE_ROD, Items.SHIELD, Items.FEATHER, Items.PISTON);
        forge(output, ModItems.REAGENT_BULLET_TIME.get(), 512, 1024, Items.CLOCK, Items.ECHO_SHARD, Items.HONEY_BLOCK, Items.COBWEB);
        forge(output, ModItems.REAGENT_PONYMEAL.get(), 32, 64, Items.BONE_BLOCK, Items.SUNFLOWER, Items.PEONY, Items.PINK_TULIP);
        forge(output, ModItems.REAGENT_FAIRYS_BLESSING.get(), 1024, 2048, ModItems.REAGENT_PONYMEAL.get(), Items.GHAST_TEAR, Items.GLISTERING_MELON_SLICE, nv("animus_mote"));
        forge(output, ModItems.REAGENT_CURSE_OF_GLUTTONY.get(), 256, 512, Items.CAKE, Items.GOLDEN_CARROT, Items.RABBIT_STEW, Items.FERMENTED_SPIDER_EYE);
        forge(output, ModItems.SPIRITUS_GEM_EXALTED.get(), 1600, 16000, nv("spiritus_gem_grand"), nv("animus_mote"), nv("deus_shard"), Items.NETHER_STAR);
        forge(output, ModItems.SPIRITUS_GEM_PRIMORDIAL.get(), 3200, 32000, ModItems.SPIRITUS_GEM_EXALTED.get(), nv("deus_shard"), Items.NETHER_STAR, Items.NETHERITE_BLOCK);

        ForgeTransformRecipe spiritualMonk = new ForgeTransformRecipe(4096, 2000,
                List.of(Ingredient.of(nv("raw_crystal_shard")), Ingredient.of(Items.BLAST_FURNACE), Ingredient.of(Items.NETHERITE_INGOT)),
                Ingredient.of(item("animusnv", "sigil_monk")),
                new ItemStack(ModItems.SIGIL_SPIRITUAL_MONK.get()));
        output.accept(id("hellfire_forge/", ModItems.SIGIL_SPIRITUAL_MONK.get()), spiritualMonk, null);
    }

    private void arrayRecipes(RecipeOutput output) {
        array(output, ModItems.SIGIL_DEMON_NINJA.get(), ModItems.REAGENT_DEMON_NINJA.get(), "tabula_animata", "sightsigil");
        array(output, ModItems.SIGIL_BERSERK.get(), ModItems.REAGENT_BERSERK.get(), "tabula_spiritus", "spikearray");
        array(output, ModItems.SIGIL_MAGICIAN.get(), ModItems.REAGENT_MAGICIAN.get(), "tabula_spiritus", "vortexsigil");
        array(output, ModItems.SIGIL_BRUNIMNEETS.get(), ModItems.REAGENT_BRUNIMNEETS.get(), "tabula_spiritus", "movementarray");
        array(output, ModItems.SIGIL_SOUL_PHOENIX.get(), ModItems.REAGENT_SOUL_PHOENIX.get(), "tabula_aetherea", "sunarray");
        array(output, ModItems.SIGIL_SANGUINE_AEGIS.get(), ModItems.REAGENT_SANGUINE_AEGIS.get(), "tabula_aetherea", "deflectionarray");
        array(output, ModItems.SIGIL_VAMPIRE_LORD.get(), ModItems.REAGENT_VAMPIRE_LORD.get(), "tabula_spiritus", "spiritsiphonarray");
        array(output, ModItems.SIGIL_ETHEREAL_STEP.get(), ModItems.REAGENT_ETHEREAL_STEP.get(), "tabula_animata", "teleportation");
        array(output, ModItems.SIGIL_SKYWARD_SOUL.get(), ModItems.REAGENT_SKYWARD_SOUL.get(), "tabula_aetherea", "airsigil");
        array(output, ModItems.SIGIL_REAPER.get(), ModItems.REAGENT_REAPER.get(), "tabula_spiritus", "collectionarray");
        array(output, ModItems.SIGIL_HELLBRAND.get(), ModItems.REAGENT_HELLBRAND.get(), "tabula_spiritus", "lavasigil");
        array(output, ModItems.SIGIL_VEIN_SEEKER.get(), ModItems.REAGENT_VEIN_SEEKER.get(), "tabula_animata", "sightsigil");
        array(output, ModItems.SIGIL_WARDING_GALE.get(), ModItems.REAGENT_WARDING_GALE.get(), "tabula_spiritus", "repulsionarray");
        array(output, ModItems.SIGIL_BULLET_TIME.get(), ModItems.REAGENT_BULLET_TIME.get(), "tabula_aetherea", "moonarray");
        array(output, ModItems.SIGIL_PONYMEAL.get(), ModItems.REAGENT_PONYMEAL.get(), "tabula_rasa", "growthsigil");
        array(output, ModItems.SIGIL_FAIRYS_BLESSING.get(), ModItems.REAGENT_FAIRYS_BLESSING.get(), "tabula_aetherea", "growtharray");
        array(output, ModItems.SIGIL_CURSE_OF_GLUTTONY.get(), ModItems.REAGENT_CURSE_OF_GLUTTONY.get(), "tabula_spiritus", "fountainarray");
    }

    private void altarRecipes(RecipeOutput output) {
        altar(output, ModItems.SIGIL_HOLDING_GREATER.get(), nv("sigil_holding"), 4, 50000, 30, 50);
        altar(output, ModItems.SIGIL_SOUL_PHOENIX_GREATER.get(), ModItems.SIGIL_SOUL_PHOENIX.get(), 5, 100000, 40, 100);
        altar(output, ModItems.SIGIL_ARCHMAGE.get(), ModItems.SIGIL_MAGICIAN.get(), 4, 60000, 40, 100);
        altar(output, ModItems.SIGIL_HOLDING_SUPREME.get(), ModItems.SIGIL_HOLDING_GREATER.get(), 5, 100000, 40, 100);
        altar(output, ModItems.SIGIL_TEMPORAL_MASTERY.get(), item("animusnv", "sigil_temporal_dominance"), 5, 100000, 40, 100);
        orb(output, ModItems.BLOOD_ORB_ASCENDANT.get(), ModItems.SPIRITUS_GEM_EXALTED.get(), 6, 500000, 150, 300);
        orb(output, ModItems.BLOOD_ORB_DIVINE.get(), ModItems.SPIRITUS_GEM_PRIMORDIAL.get(), 7, 1200000, 200, 400);
    }

    private static void forge(RecipeOutput output, Item result, double drain, double minDrain, ItemLike... inputs) {
        List<Ingredient> ingredients = Arrays.stream(inputs).map(Ingredient::of).toList();
        ForgeRecipe recipe = new ForgeRecipe(minDrain, drain, ingredients, new ItemStack(result), Optional.empty());
        output.accept(id("hellfire_forge/", result), recipe, null);
    }

    private static void array(RecipeOutput output, Item result, Item reagent, String tabula, String texture) {
        AlchemyArrayRecipe recipe = new AlchemyArrayRecipe(
                ResourceLocation.fromNamespaceAndPath("neovitae", "textures/models/alchemyarrays/" + texture + ".png"),
                Ingredient.of(reagent), Ingredient.of(nv(tabula)), new ItemStack(result));
        output.accept(id("array/", result), recipe, null);
    }

    private static void altar(RecipeOutput output, Item result, ItemLike input, int tier, int blood, int craftSpeed, int drainSpeed) {
        AraVitaeRecipe recipe = new AraVitaeRecipe(Ingredient.of(input), new ItemStack(result), tier, blood, craftSpeed, drainSpeed, true);
        output.accept(id("ara_vitae/", result), recipe, null);
    }

    private static void orb(RecipeOutput output, Item result, ItemLike input, int tier, int blood, int craftSpeed, int drainSpeed) {
        AraVitaeRecipe recipe = new AraVitaeRecipe(Ingredient.of(input), new ItemStack(result), tier, blood, craftSpeed, drainSpeed, false);
        output.accept(id("ara_vitae/", result), recipe, null);
    }

    private static ResourceLocation id(String folder, Item item) {
        return NotEnoughSigils.rl(folder + BuiltInRegistries.ITEM.getKey(item).getPath());
    }

    private static Item nv(String path) {
        return item("neovitae", path);
    }

    private static Item item(String namespace, String path) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, path);
        if (!BuiltInRegistries.ITEM.containsKey(location)) {
            throw new IllegalStateException("Missing item " + location);
        }
        return BuiltInRegistries.ITEM.get(location);
    }
}
