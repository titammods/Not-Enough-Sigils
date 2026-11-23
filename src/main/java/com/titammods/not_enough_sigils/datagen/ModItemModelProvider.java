package com.titammods.not_enough_sigils.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModItems;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModItemModelProvider implements DataProvider {
    private static final List<String> SPIRITUS_VARIANTS = List.of("raw", "ruina", "nihilum", "invictus", "vindicta");
    private static final String SPIRITUS_COMPONENT = "neovitae:spiritus_type";

    private final PackOutput.PathProvider modelPath;
    private final PackOutput.PathProvider itemPath;

    public ModItemModelProvider(PackOutput output) {
        this.modelPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
        this.itemPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        Set<Item> gems = Set.of(ModItems.SPIRITUS_GEM_EXALTED.get(), ModItems.SPIRITUS_GEM_PRIMORDIAL.get());
        for (DeferredHolder<Item, ? extends Item> holder : ModItems.ITEMS.getEntries()) {
            String name = holder.getId().getPath();
            if (gems.contains(holder.get())) {
                spiritusGem(cache, futures, name);
            } else {
                futures.add(DataProvider.saveStable(cache, layeredModel("minecraft:item/generated", "item/" + name), modelPath.json(id(name))));
                futures.add(DataProvider.saveStable(cache, plainItem("item/" + name), itemPath.json(id(name))));
            }
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private void spiritusGem(CachedOutput cache, List<CompletableFuture<?>> futures, String name) {
        JsonArray cases = new JsonArray();
        for (String variant : SPIRITUS_VARIANTS) {
            String model = "item/variant/" + name + "_" + variant;
            futures.add(DataProvider.saveStable(cache, layeredModel("minecraft:item/handheld", "item/" + name + "_" + variant), modelPath.json(id("variant/" + name + "_" + variant))));
            if (!variant.equals(SPIRITUS_VARIANTS.get(0))) {
                JsonObject entry = new JsonObject();
                entry.addProperty("when", variant);
                entry.add("model", modelReference(model));
                cases.add(entry);
            }
        }
        futures.add(DataProvider.saveStable(cache, layeredModel("minecraft:item/handheld", "item/" + name + "_" + SPIRITUS_VARIANTS.get(0)), modelPath.json(id(name))));

        JsonObject select = new JsonObject();
        select.addProperty("type", "minecraft:select");
        select.addProperty("property", "minecraft:component");
        select.addProperty("component", SPIRITUS_COMPONENT);
        select.add("cases", cases);
        select.add("fallback", modelReference("item/variant/" + name + "_" + SPIRITUS_VARIANTS.get(0)));

        JsonObject root = new JsonObject();
        root.add("model", select);
        futures.add(DataProvider.saveStable(cache, root, itemPath.json(id(name))));
    }

    private static JsonObject layeredModel(String parent, String texture) {
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", NotEnoughSigils.MODID + ":" + texture);
        JsonObject model = new JsonObject();
        model.addProperty("parent", parent);
        model.add("textures", textures);
        return model;
    }

    private static JsonObject plainItem(String model) {
        JsonObject root = new JsonObject();
        root.add("model", modelReference(model));
        return root;
    }

    private static JsonObject modelReference(String model) {
        JsonObject reference = new JsonObject();
        reference.addProperty("type", "minecraft:model");
        reference.addProperty("model", NotEnoughSigils.MODID + ":" + model);
        return reference;
    }

    private static Identifier id(String path) {
        return NotEnoughSigils.rl(path);
    }

    @Override
    public String getName() {
        return "Not Enough Sigils Item Models";
    }
}
