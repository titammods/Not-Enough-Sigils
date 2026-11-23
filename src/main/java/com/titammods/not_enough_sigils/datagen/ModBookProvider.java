package com.titammods.not_enough_sigils.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.titammods.not_enough_sigils.altar.AltarBookSync;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ModBookProvider implements DataProvider {
    private static final String KEY = "book.not_enough_sigils.guide.altar.ascended_tiers.";
    private static final String[] SOURCE = {"data", "neovitae", "modonomicon", "books", "guide", "entries", "altar", "ara_vitae.json"};
    private static final ResourceLocation ENTRY = ResourceLocation.fromNamespaceAndPath("neovitae", "guide/entries/altar/ara_vitae");
    private static final String LAST_NATIVE_MULTIBLOCK = "altar_six";

    private final PackOutput.PathProvider pathProvider;

    public ModBookProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "modonomicon/books");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        JsonObject entry;
        try {
            entry = readNativeEntry();
        } catch (IOException exception) {
            return CompletableFuture.failedFuture(exception);
        }
        JsonArray pages = entry.getAsJsonArray("pages");
        JsonArray patched = new JsonArray();
        boolean inserted = false;
        for (JsonElement page : pages) {
            patched.add(page);
            if (!inserted && isMultiblock(page, LAST_NATIVE_MULTIBLOCK)) {
                appendAscendedPages(patched);
                inserted = true;
            }
        }
        if (!inserted) {
            appendAscendedPages(patched);
        }
        entry.add("pages", patched);
        return DataProvider.saveStable(output, entry, pathProvider.json(ENTRY));
    }

    private static JsonObject readNativeEntry() throws IOException {
        IModFileInfo file = ModList.get().getModFileById("neovitae");
        if (file == null) {
            throw new IOException("Neo Vitae is not loaded");
        }
        Path path = file.getFile().findResource(SOURCE);
        if (!Files.exists(path)) {
            throw new IOException("Missing Neo Vitae book entry " + path);
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static boolean isMultiblock(JsonElement page, String id) {
        if (!page.isJsonObject()) {
            return false;
        }
        JsonObject object = page.getAsJsonObject();
        if (!object.has("multiblock_id")) {
            return false;
        }
        String value = object.get("multiblock_id").getAsString();
        return value.equals(id) || value.equals("neovitae:" + id);
    }

    private static void appendAscendedPages(JsonArray pages) {
        pages.add(text(KEY + "intro.title", KEY + "intro.text"));
        pages.add(text("", KEY + "tier6_text.text"));
        pages.add(multiblock(AltarBookSync.ASCENDANT_MULTIBLOCK, KEY + "tier6.title", KEY + "tier6.text"));
        pages.add(text("", KEY + "tier7_text.text"));
        pages.add(multiblock(AltarBookSync.DIVINE_MULTIBLOCK, KEY + "tier7.title", KEY + "tier7.text"));
    }

    private static JsonObject text(String title, String text) {
        JsonObject page = basePage("modonomicon:text");
        page.addProperty("title", title);
        page.addProperty("text", text);
        page.addProperty("show_title_separator", true);
        page.addProperty("use_markdown_in_title", false);
        return page;
    }

    private static JsonObject multiblock(ResourceLocation id, String name, String text) {
        JsonObject page = basePage("modonomicon:multiblock");
        page.addProperty("multiblock_id", id.toString());
        page.addProperty("multiblock_name", name);
        page.addProperty("text", text);
        page.addProperty("show_visualize_button", true);
        return page;
    }

    private static JsonObject basePage(String type) {
        JsonObject page = new JsonObject();
        page.addProperty("type", type);
        page.addProperty("anchor", "");
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "modonomicon:none");
        page.add("condition", condition);
        return page;
    }

    @Override
    public String getName() {
        return "Not Enough Sigils Book Entries";
    }
}
