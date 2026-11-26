package com.titammods.not_enough_sigils.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.altar.AltarBookSync;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModBookProvider implements DataProvider {
    private static final String BOOK = "guide";
    private static final String PREFIX = "book." + NotEnoughSigils.MODID + "." + BOOK + ".";
    private static final String GUI = NotEnoughSigils.MODID + ":textures/gui/";
    private static final String[] BASIC_SIGILS = {
            "soul_phoenix", "demon_ninja", "berserk", "magician", "brunimneets", "sanguine_aegis",
            "vampire_lord", "ethereal_step", "skyward_soul", "reaper", "hellbrand", "vein_seeker",
            "warding_gale", "bullet_time", "ponymeal", "curse_of_gluttony", "fairys_blessing", "recall", "alchemist", "sanctuary", "executioner", "chains", "harvest", "quicksilver"
    };
    private static final String[] ALTAR_UPGRADES = {
            "soul_phoenix_greater", "archmage", "berserk_divine", "fairys_blessing_divine", "temporal_mastery"
    };
    private static final String[] FORGE_UPGRADES = {
            "ethereal_step_greater", "vein_seeker_greater", "bullet_time_greater", "warding_gale_greater", "sanguine_aegis_greater",
            "vampire_lord_greater", "hellbrand_greater", "reaper_greater", "skyward_soul_greater", "demon_ninja_greater", "recall_greater"
    };
    private static final String[] HOLDING = {"holding_greater", "holding_supreme", "holding_divine"};

    private final PackOutput.PathProvider pathProvider;

    public ModBookProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "modonomicon/books");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> saves = new ArrayList<>();
        saves.add(save(cache, book(), "book"));
        List<Category> categories = List.of(sigils(), upgrades(), altar(), spiritus(), saereth(), rituals());
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            saves.add(save(cache, category.json(i), "categories/" + category.id));
            for (Entry entry : category.entries) {
                saves.add(save(cache, entry.json(), "entries/" + category.id + "/" + entry.id));
            }
        }
        return CompletableFuture.allOf(saves.toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> save(CachedOutput cache, JsonObject json, String path) {
        return DataProvider.saveStable(cache, json, pathProvider.json(NotEnoughSigils.rl(BOOK + "/" + path)));
    }

    private static Category sigils() {
        Category category = new Category("sigils", own("sigil_soul_phoenix"), "sigils");
        Entry intro = category.add("introduction", own("reagent_soul_phoenix"), 0, -4)
                .text("intro", true).text("reagents", false).text("arrays", false);
        for (int i = 0; i < BASIC_SIGILS.length; i++) {
            String sigil = BASIC_SIGILS[i];
            Entry entry = category.add(sigil, own("sigil_" + sigil), -6 + (i % 7) * 2, -1 + (i / 7) * 2).parent(intro)
                    .text("effect", true);
            if (sigil.equals("reaper")) {
                entry.text("fragments", false);
            }
            entry.forge(recipe("hellfire_forge/reagent_" + sigil))
                    .array(recipe("array/sigil_" + sigil));
        }
        return category;
    }

    private static Category upgrades() {
        Category category = new Category("upgrades", own("sigil_archmage"), "upgrades");
        Entry intro = category.add("introduction", own("sigil_soul_phoenix_greater"), 0, -4)
                .text("intro", true).text("altar", false);
        for (int i = 0; i < ALTAR_UPGRADES.length; i++) {
            String sigil = ALTAR_UPGRADES[i];
            category.add(sigil, own("sigil_" + sigil), -5 + i * 2, -1).parent(intro)
                    .text("effect", true)
                    .altar(recipe("ara_vitae/sigil_" + sigil));
        }
        category.add("spiritual_monk", own("sigil_spiritual_monk"), 5, -1).parent(intro)
                .text("effect", true).text("modes", false)
                .forge(recipe("hellfire_forge/sigil_spiritual_monk"));
        Entry previous = intro;
        for (int i = 0; i < HOLDING.length; i++) {
            String sigil = HOLDING[i];
            previous = category.add(sigil, own("sigil_" + sigil), -2 + i * 2, 2).parent(previous)
                    .text("effect", true)
                    .altar(recipe("ara_vitae/sigil_" + sigil));
        }
        for (int i = 0; i < FORGE_UPGRADES.length; i++) {
            String sigil = FORGE_UPGRADES[i];
            category.add(sigil, own("sigil_" + sigil), -6 + (i % 7) * 2, 5 + (i / 7) * 2).parent(intro)
                    .text("effect", true)
                    .forge(recipe("hellfire_forge/sigil_" + sigil));
        }
        return category;
    }

    private static Category altar() {
        Category category = new Category("altar", "neovitae:ara_vitae", "altar");
        Entry tiers = category.add("ascended_tiers", "neovitae:ara_vitae", 0, -3)
                .text("intro", true)
                .tierInfo("tier_table")
                .text("tier6_text", false)
                .multiblock("tier6", AltarBookSync.ASCENDANT_MULTIBLOCK)
                .text("tier7_text", false)
                .multiblock("tier7", AltarBookSync.DIVINE_MULTIBLOCK);
        category.add("blood_orbs", own("blood_orb_divine"), -3, 0).parent(tiers)
                .text("intro", true)
                .text("ascendant", false)
                .altar(recipe("ara_vitae/blood_orb_ascendant"))
                .text("divine", false)
                .altar(recipe("ara_vitae/blood_orb_divine"))
                .orbStats("orb_tiers");
        category.add("ascended_runes", own("rune_eternity"), 3, 0).parent(tiers)
                .text("intro", true)
                .text("capacity", false)
                .crafting(recipe("rune_3_capacity_augmented"))
                .text("speed", false)
                .crafting(recipe("rune_3_speed"))
                .text("eternity", false)
                .crafting(recipe("rune_eternity"))
                .text("sacrifice", false)
                .crafting(recipe("rune_3_sacrifice"))
                .text("sacrifice_self", false)
                .crafting(recipe("rune_3_sacrifice_self"))
                .text("orb", false)
                .crafting(recipe("rune_3_orb"))
                .text("dislocation", false)
                .crafting(recipe("rune_3_dislocation"))
                .text("charging", false)
                .crafting(recipe("rune_3_charging"));
        category.add("progression", own("divine_essence"), 0, 3).parent(tiers)
                .text("intro", true)
                .text("soul_fragment", false)
                .text("ascended", false)
                .altar(recipe("ara_vitae/ascended_essence"))
                .text("divine", false)
                .altar(recipe("ara_vitae/divine_essence"));
        return category;
    }

    private static Category spiritus() {
        Category category = new Category("spiritus", own("spiritus_gem_primordial"), "spiritus");
        Entry gems = category.add("ascended_gems", own("spiritus_gem_primordial"), 0, -2)
                .text("intro", true)
                .text("exalted", false)
                .forge(recipe("hellfire_forge/spiritus_gem_exalted"))
                .text("primordial", false)
                .forge(recipe("hellfire_forge/spiritus_gem_primordial"))
                .gemStats("gem_stats");
        category.add("spiritus_sigils", own("sigil_magician"), 0, 1).parent(gems)
                .text("intro", true).text("sigils", false);
        return category;
    }

    private static Category rituals() {
        Category category = new Category("rituals", "neovitae:master_ritual_stone", "saereth");
        category.add("builder", "minecraft:netherite_pickaxe", 0, 0)
                .text("intro", true)
                .text("area", false)
                .text("mining", false)
                .ritualInfo("info");
        return category;
    }

    private static Category saereth() {
        Category category = new Category("saereth", own("sigil_soul_capture"), "saereth");
        Entry ritual = category.add("ritual", "neovitae:master_ritual_stone", 0, -3)
                .text("intro", true)
                .ritualInfo("info");
        category.add("sigil", own("sigil_soul_capture"), -3, 0).parent(ritual)
                .text("intro", true).text("linking", false).text("capture", false);
        category.add("workshop", own("sigil_soul_capture"), 3, 0).parent(ritual)
                .entity("saereth", NotEnoughSigils.rl("saereth"))
                .text("crafting", true).text("status", false);
        category.add("library", "minecraft:enchanted_book", 0, 3).parent(ritual)
                .text("intro", true).text("rules", false).text("settings", false);
        return category;
    }

    private static JsonObject book() {
        JsonObject json = new JsonObject();
        json.addProperty("allow_open_book_with_invalid_links", false);
        json.addProperty("auto_add_read_conditions", false);
        json.addProperty("book_content_texture", GUI + "book_content.png");
        json.addProperty("book_overview_texture", GUI + "book_overview.png");
        json.addProperty("book_text_offset_height", 0);
        json.addProperty("book_text_offset_width", -10);
        json.addProperty("book_text_offset_x", 5);
        json.addProperty("book_text_offset_y", 0);
        json.add("bottom_frame_overlay", overlay("bottom", 8, 72, 0, -4));
        json.addProperty("category_button_icon_scale", 1.0);
        json.addProperty("category_button_x_offset", 0);
        json.addProperty("category_button_y_offset", 0);
        json.addProperty("crafting_texture", GUI + "crafting_textures.png");
        json.addProperty("creative_tab", NotEnoughSigils.MODID + ":main");
        json.addProperty("custom_book_item", NotEnoughSigils.MODID + ":guide_book");
        json.addProperty("default_text_color", 0);
        json.addProperty("default_title_color", 0);
        json.addProperty("description", PREFIX + "description");
        json.addProperty("display_mode", "node");
        json.addProperty("font", "minecraft:default");
        json.addProperty("frame_texture", GUI + "book_frame.png");
        json.addProperty("generate_book_item", false);
        json.add("left_frame_overlay", overlay("left", 70, 7, 3, 0));
        json.addProperty("model", "modonomicon:modonomicon_purple");
        json.addProperty("name", PREFIX + "name");
        json.addProperty("page_display_mode", "double_page");
        json.addProperty("read_all_button_y_offset", 0);
        json.add("right_frame_overlay", overlay("right", 70, 8, -4, 0));
        json.addProperty("search_button_x_offset", 0);
        json.addProperty("search_button_y_offset", 0);
        json.addProperty("single_page_texture", "modonomicon:textures/gui/single_page_entry.png");
        json.addProperty("tooltip", PREFIX + "tooltip");
        json.add("top_frame_overlay", overlay("top", 7, 72, 0, 4));
        json.addProperty("turn_page_sound", "modonomicon:turn_page");
        return json;
    }

    private static JsonObject overlay(String side, int height, int width, int x, int y) {
        JsonObject json = new JsonObject();
        json.addProperty("frame_height", height);
        json.addProperty("frame_width", width);
        json.addProperty("frame_x_offset", x);
        json.addProperty("frame_y_offset", y);
        json.addProperty("texture", "modonomicon:textures/gui/book_frame_" + side + "_overlay.png");
        json.addProperty("texture_height", 256);
        json.addProperty("texture_width", 256);
        return json;
    }

    private static String own(String path) {
        return NotEnoughSigils.MODID + ":" + path;
    }

    private static String recipe(String path) {
        return own(path);
    }

    private static JsonObject icon(String item) {
        JsonObject json = new JsonObject();
        json.addProperty("item", item);
        return json;
    }

    private static JsonObject condition(String parent) {
        JsonObject json = new JsonObject();
        if (parent == null) {
            json.addProperty("type", "modonomicon:none");
        } else {
            json.addProperty("type", "modonomicon:entry_read");
            json.addProperty("entry_id", parent);
        }
        return json;
    }

    private static class Category {
        private final String id;
        private final String icon;
        private final String parallax;
        private final List<Entry> entries = new ArrayList<>();

        private Category(String id, String icon, String parallax) {
            this.id = id;
            this.icon = icon;
            this.parallax = parallax;
        }

        private Entry add(String entryId, String entryIcon, int x, int y) {
            Entry entry = new Entry(this.id, entryId, entryIcon, x, y, this.entries.size());
            this.entries.add(entry);
            return entry;
        }

        private JsonObject json(int sort) {
            JsonObject json = new JsonObject();
            json.addProperty("background", "modonomicon:textures/gui/dark_slate_seamless.png");
            json.addProperty("background_height", 512);
            JsonArray layers = new JsonArray();
            for (String layer : new String[]{"base", "layer_1", "layer_2"}) {
                JsonObject object = new JsonObject();
                object.addProperty("background", GUI + "parallax/" + this.parallax + "_" + layer + ".png");
                layers.add(object);
            }
            json.add("background_parallax_layers", layers);
            json.addProperty("background_texture_zoom_multiplier", 1.0);
            json.addProperty("background_width", 512);
            json.addProperty("description", PREFIX + this.id + ".description");
            json.addProperty("display_mode", "node");
            json.addProperty("entry_textures", "modonomicon:textures/gui/entry_textures.png");
            json.add("icon", icon(this.icon));
            json.addProperty("max_scroll_x", 512);
            json.addProperty("max_scroll_y", 512);
            json.addProperty("name", PREFIX + this.id + ".name");
            json.addProperty("show_category_button", true);
            json.addProperty("sort_number", sort);
            return json;
        }
    }

    private static class Entry {
        private final String category;
        private final String id;
        private final String icon;
        private final int x;
        private final int y;
        private final int sort;
        private final JsonArray pages = new JsonArray();
        private String parent;

        private Entry(String category, String id, String icon, int x, int y, int sort) {
            this.category = category;
            this.id = id;
            this.icon = icon;
            this.x = x;
            this.y = y;
            this.sort = sort;
        }

        private String key() {
            return PREFIX + this.category + "." + this.id + ".";
        }

        private String path() {
            return this.category + "/" + this.id;
        }

        private Entry parent(Entry entry) {
            this.parent = entry.path();
            return this;
        }

        private Entry text(String page, boolean titled) {
            JsonObject json = page("modonomicon:text");
            json.addProperty("show_title_separator", true);
            json.addProperty("text", key() + page + ".text");
            json.addProperty("title", titled ? key() + page + ".title" : "");
            json.addProperty("use_markdown_in_title", false);
            return add(json);
        }

        private Entry forge(String recipe) {
            return add(recipePage("neovitae:hellfire_forge_recipe", recipe));
        }

        private Entry array(String recipe) {
            return add(recipePage("neovitae:alchemy_array_recipe", recipe));
        }

        private Entry altar(String recipe) {
            return add(recipePage("neovitae:ara_vitae_recipe", recipe));
        }

        private Entry crafting(String recipe) {
            return add(recipePage("modonomicon:crafting_recipe", recipe));
        }

        private Entry multiblock(String page, ResourceLocation multiblock) {
            JsonObject json = page("modonomicon:multiblock");
            json.addProperty("multiblock_id", multiblock.toString());
            json.addProperty("multiblock_name", key() + page + ".title");
            json.addProperty("show_visualize_button", true);
            json.addProperty("text", key() + page + ".text");
            return add(json);
        }

        private Entry tierInfo(String page) {
            return titled("neovitae:altar_tier_info", page);
        }

        private Entry orbStats(String page) {
            return titled("neovitae:blood_orb_stats", page);
        }

        private Entry gemStats(String page) {
            return titled("neovitae:spiritus_gem_stats", page);
        }

        private Entry ritualInfo(String page) {
            JsonObject json = page("neovitae:ritual_info");
            json.addProperty("text", key() + page + ".text");
            json.addProperty("title", key() + page + ".title");
            return add(json);
        }

        private Entry entity(String page, ResourceLocation entity) {
            JsonObject json = page("modonomicon:entity");
            json.addProperty("default_rotation", -45.0);
            json.addProperty("entity_id", entity.toString());
            json.addProperty("name", key() + page + ".title");
            json.addProperty("offset", 0.0);
            json.addProperty("rotate", true);
            json.addProperty("scale", 0.8);
            json.addProperty("text", key() + page + ".text");
            return add(json);
        }

        private Entry titled(String type, String page) {
            JsonObject json = page(type);
            json.addProperty("title", key() + page + ".title");
            return add(json);
        }

        private Entry add(JsonObject page) {
            this.pages.add(page);
            return this;
        }

        private static JsonObject recipePage(String type, String recipe) {
            JsonObject json = page(type);
            json.addProperty("recipe_id_1", recipe);
            json.addProperty("text", "");
            json.addProperty("title1", "");
            json.addProperty("title2", "");
            return json;
        }

        private static JsonObject page(String type) {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("anchor", "");
            json.add("condition", condition(null));
            return json;
        }

        private JsonObject json() {
            JsonObject json = new JsonObject();
            json.addProperty("background_u_index", 0);
            json.addProperty("background_v_index", 0);
            json.addProperty("category", this.category);
            json.add("condition", condition(this.parent));
            json.addProperty("description", key() + "description");
            json.addProperty("hide_while_locked", false);
            json.add("icon", icon(this.icon));
            json.addProperty("name", key() + "name");
            if (this.parent != null) {
                JsonArray parents = new JsonArray();
                JsonObject link = new JsonObject();
                link.addProperty("draw_arrow", true);
                link.addProperty("entry", this.parent);
                link.addProperty("line_enabled", true);
                link.addProperty("line_reversed", false);
                parents.add(link);
                json.add("parents", parents);
            }
            json.add("pages", this.pages);
            json.addProperty("show_when_any_parent_unlocked", false);
            json.addProperty("sort_number", this.sort);
            json.addProperty("x", this.x);
            json.addProperty("y", this.y);
            return json;
        }
    }

    @Override
    public String getName() {
        return "Not Enough Sigils Guide Book";
    }
}
