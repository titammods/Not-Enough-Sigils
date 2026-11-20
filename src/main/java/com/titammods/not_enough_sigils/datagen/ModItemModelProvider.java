package com.titammods.not_enough_sigils.datagen;

import com.breakinblocks.neovitae.NeoVitae;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;

public class ModItemModelProvider extends ItemModelProvider {
    private static final List<String> SPIRITUS_VARIANTS = List.of("raw", "ruina", "nihilum", "invictus", "vindicta");

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NotEnoughSigils.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        Set<Item> gems = Set.of(ModItems.SPIRITUS_GEM_EXALTED.get(), ModItems.SPIRITUS_GEM_PRIMORDIAL.get());
        for (DeferredHolder<Item, ? extends Item> holder : ModItems.ITEMS.getEntries()) {
            Item item = holder.get();
            if (gems.contains(item)) {
                spiritusGem(holder.getId().getPath());
            } else {
                basicItem(item);
            }
        }
    }

    private void spiritusGem(String name) {
        ItemModelBuilder base = withExistingParent(name, mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/" + name + "_" + SPIRITUS_VARIANTS.get(0)));
        for (int i = 0; i < SPIRITUS_VARIANTS.size(); i++) {
            String variant = "item/variant/" + name + "_" + SPIRITUS_VARIANTS.get(i);
            withExistingParent(variant, mcLoc("item/handheld"))
                    .texture("layer0", modLoc("item/" + name + "_" + SPIRITUS_VARIANTS.get(i)));
            base.override()
                    .predicate(NeoVitae.TYPE_PROPERTY, i)
                    .model(new ModelFile.UncheckedModelFile(modLoc(variant)))
                    .end();
        }
    }
}
