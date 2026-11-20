package com.titammods.not_enough_sigils.datagen;

import com.breakinblocks.neovitae.common.tag.NVTags;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    private static final TagKey<Item> CURIOS_CHARM = curios("charm");
    private static final TagKey<Item> CURIOS_NECKLACE = curios("necklace");
    private static final TagKey<Item> CURIOS_SENTIENT_SOCKET = curios("sentient_armour_socket");

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, NotEnoughSigils.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(CURIOS_CHARM).add(
                ModItems.SIGIL_SOUL_PHOENIX.get(),
                ModItems.SIGIL_DEMON_NINJA.get(),
                ModItems.SIGIL_BERSERK.get(),
                ModItems.SIGIL_BRUNIMNEETS.get(),
                ModItems.SIGIL_HOLDING_GREATER.get(),
                ModItems.SIGIL_SANGUINE_AEGIS.get(),
                ModItems.SIGIL_VAMPIRE_LORD.get(),
                ModItems.SIGIL_SKYWARD_SOUL.get(),
                ModItems.SIGIL_REAPER.get(),
                ModItems.SIGIL_HELLBRAND.get(),
                ModItems.SIGIL_WARDING_GALE.get(),
                ModItems.SIGIL_SOUL_PHOENIX_GREATER.get(),
                ModItems.SIGIL_HOLDING_SUPREME.get(),
                ModItems.SIGIL_SPIRITUAL_MONK.get()
        );
        tag(CURIOS_NECKLACE).add(ModItems.SPIRITUS_GEM_EXALTED.get(), ModItems.SPIRITUS_GEM_PRIMORDIAL.get());
        tag(CURIOS_SENTIENT_SOCKET).add(ModItems.SPIRITUS_GEM_EXALTED.get(), ModItems.SPIRITUS_GEM_PRIMORDIAL.get());
        tag(NVTags.Items.SPIRITUS_GEM).add(ModItems.SPIRITUS_GEM_EXALTED.get(), ModItems.SPIRITUS_GEM_PRIMORDIAL.get());
        tag(Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS).add(
                ModItems.NINJA_KNIFE.get(),
                ModItems.NINJA_KNIFE_EXPLOSIVE.get(),
                ModItems.NINJA_KNIFE_LIGHTNING.get(),
                ModItems.NINJA_KNIFE_FROST.get(),
                ModItems.NINJA_KNIFE_FIRE.get()
        );
    }

    private static TagKey<Item> curios(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", path));
    }
}
