package com.titammods.not_enough_sigils.altar;

import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.registry.AltarEffect;
import com.breakinblocks.neovitae.common.registry.AltarEffectType;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs.TagOrElementLocation;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModAltarTiers {
    public static final int ASCENDANT_TIER = 6;
    public static final int DIVINE_TIER = 7;

    public static final ResourceKey<AltarTier> ASCENDANT = ResourceKey.create(NVRegistries.Keys.ALTAR_TIER_KEY, NotEnoughSigils.rl("ascendant"));
    public static final ResourceKey<AltarTier> DIVINE = ResourceKey.create(NVRegistries.Keys.ALTAR_TIER_KEY, NotEnoughSigils.rl("divine"));

    private static final TagOrElementLocation ALTAR = new TagOrElementLocation(ResourceLocation.fromNamespaceAndPath("neovitae", "ara_vitae"), false);
    private static final TagOrElementLocation RUNE = tag(NVTags.Blocks.RUNES);
    private static final TagOrElementLocation PILLAR = tag(NVTags.Blocks.PILLARS);
    private static final TagOrElementLocation T3_CAP = tag(NVTags.Blocks.T3_CAPSTONES);
    private static final TagOrElementLocation T4_CAP = tag(NVTags.Blocks.T4_CAPSTONES);
    private static final TagOrElementLocation T5_CAP = tag(NVTags.Blocks.T5_CAPSTONES);
    private static final TagOrElementLocation T6_CAP = tag(NVTags.Blocks.T6_CAPSTONES);

    private static final List<BlockPos> MAGE_CAPS = cardinals(4, 1);
    private static final List<BlockPos> MASTER_CAPS = corners(6, 2);
    private static final List<BlockPos> ARCHMAGE_CAPS = cardinals(9, -4);
    private static final List<BlockPos> TRANSCENDENT_CAPS = cardinals(12, 3);
    private static final List<BlockPos> ASCENDANT_CAPS = corners(10, 4);
    private static final List<BlockPos> DIVINE_CAPS = cardinals(17, 6);
    private static final List<BlockPos> DIVINE_CORNERS = corners(12, -6);

    public static void bootstrap(BootstrapContext<AltarTier> context) {
        context.register(ASCENDANT, new AltarTier(ASCENDANT_TIER, ascendantComponents(), ascendantEffects()));
        context.register(DIVINE, new AltarTier(DIVINE_TIER, divineComponents(), divineEffects()));
    }

    public static List<AltarComponent> transcendentComponents() {
        List<AltarComponent> out = new ArrayList<>();
        out.add(new AltarComponent(BlockPos.ZERO, ALTAR, false));
        addRing(out, 1, -1);
        addRing(out, 3, -2);
        addCardinalPillars(out, 4, -1, 0);
        addCaps(out, MAGE_CAPS, T3_CAP);
        addRing(out, 5, -3);
        addCaps(out, MASTER_CAPS, T4_CAP);
        addRing(out, 8, -4);
        addCaps(out, ARCHMAGE_CAPS, T5_CAP);
        addRing(out, 11, -5);
        addCardinalPillars(out, 12, -4, 2);
        addCaps(out, TRANSCENDENT_CAPS, T6_CAP);
        return out;
    }

    public static List<AltarComponent> ascendantComponents() {
        List<AltarComponent> out = transcendentComponents();
        addRing(out, 14, -6);
        addCornerPillars(out, 10, -5, 3);
        out.add(new AltarComponent(new BlockPos(10, 4, 10), tag(ModTags.Blocks.T7_CAPSTONES_RUINA), false));
        out.add(new AltarComponent(new BlockPos(-10, 4, 10), tag(ModTags.Blocks.T7_CAPSTONES_NIHILUM), false));
        out.add(new AltarComponent(new BlockPos(-10, 4, -10), tag(ModTags.Blocks.T7_CAPSTONES_INVICTUS), false));
        out.add(new AltarComponent(new BlockPos(10, 4, -10), tag(ModTags.Blocks.T7_CAPSTONES_VINDICTA), false));
        return out;
    }

    public static List<AltarComponent> divineComponents() {
        List<AltarComponent> out = ascendantComponents();
        addRing(out, 17, -7);
        addCardinalPillars(out, 17, -6, 5);
        addCaps(out, DIVINE_CAPS, tag(ModTags.Blocks.T8_CAPSTONES));
        addCaps(out, DIVINE_CORNERS, tag(ModTags.Blocks.T8_CORNERSTONES));
        return out;
    }

    private static List<AltarEffect> transcendentEffects() {
        List<AltarEffect> out = new ArrayList<>();
        out.add(new AltarEffect(AltarEffectType.CAP_ORBIT_LIFE_PULSE, MAGE_CAPS, 0xBB3300));
        out.add(new AltarEffect(AltarEffectType.CAP_ORBIT_SPIRAL_STAGGERED, MASTER_CAPS, 0x880011));
        out.add(new AltarEffect(AltarEffectType.CAP_BURST, ARCHMAGE_CAPS, 0x8800CC));
        out.add(new AltarEffect(AltarEffectType.CAP_RENDER_HOVER_ARRAY, ARCHMAGE_CAPS, 0xFFFFFF));
        out.add(new AltarEffect(AltarEffectType.CAP_CRYSTAL_CASCADE, TRANSCENDENT_CAPS, 0x8800CC));
        return out;
    }

    private static List<AltarEffect> ascendantEffects() {
        List<AltarEffect> out = transcendentEffects();
        out.add(new AltarEffect(AltarEffectType.CAP_CRYSTAL_CASCADE, ASCENDANT_CAPS, 0x33CCAA));
        out.add(new AltarEffect(AltarEffectType.CAP_ORBIT_SPIRAL_STAGGERED, ASCENDANT_CAPS, 0x6633CC));
        return List.copyOf(out);
    }

    private static List<AltarEffect> divineEffects() {
        List<AltarEffect> out = new ArrayList<>(ascendantEffects());
        out.add(new AltarEffect(AltarEffectType.CAP_BURST, DIVINE_CAPS, 0xFFCC33));
        out.add(new AltarEffect(AltarEffectType.CAP_RENDER_HOVER_ARRAY, DIVINE_CAPS, 0xFFDD88));
        out.add(new AltarEffect(AltarEffectType.CAP_ORBIT_LIFE_PULSE, DIVINE_CORNERS, 0xCC2244));
        return List.copyOf(out);
    }

    private static TagOrElementLocation tag(TagKey<Block> tag) {
        return new TagOrElementLocation(tag.location(), true);
    }

    private static void addRing(List<AltarComponent> out, int radius, int y) {
        int rSq = radius * radius;
        int lo = rSq - radius + 1;
        int hi = rSq + radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int d = x * x + z * z;
                if (d >= lo && d <= hi) {
                    out.add(new AltarComponent(new BlockPos(x, y, z), RUNE, true));
                }
            }
        }
    }

    private static void addCardinalPillars(List<AltarComponent> out, int distance, int yLo, int yHi) {
        addPillars(out, cardinals(distance, 0), yLo, yHi);
    }

    private static void addCornerPillars(List<AltarComponent> out, int distance, int yLo, int yHi) {
        addPillars(out, corners(distance, 0), yLo, yHi);
    }

    private static void addPillars(List<AltarComponent> out, List<BlockPos> columns, int yLo, int yHi) {
        for (BlockPos column : columns) {
            for (int y = yLo; y <= yHi; y++) {
                out.add(new AltarComponent(new BlockPos(column.getX(), y, column.getZ()), PILLAR, false, true));
            }
        }
    }

    private static void addCaps(List<AltarComponent> out, List<BlockPos> positions, TagOrElementLocation material) {
        for (BlockPos pos : positions) {
            out.add(new AltarComponent(pos, material, false));
        }
    }

    private static List<BlockPos> cardinals(int distance, int y) {
        return List.of(new BlockPos(distance, y, 0), new BlockPos(-distance, y, 0), new BlockPos(0, y, distance), new BlockPos(0, y, -distance));
    }

    private static List<BlockPos> corners(int distance, int y) {
        return List.of(new BlockPos(distance, y, distance), new BlockPos(-distance, y, distance), new BlockPos(distance, y, -distance), new BlockPos(-distance, y, -distance));
    }
}
