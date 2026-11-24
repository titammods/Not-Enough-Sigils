package com.titammods.not_enough_sigils.altar;

import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.altar.rune.AltarRuneModifiers;
import com.breakinblocks.neovitae.api.altar.rune.EnumAltarRuneType;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneRegistry;
import com.breakinblocks.neovitae.api.altar.rune.RuneInstance;
import com.breakinblocks.neovitae.api.event.AltarRuneEvent;
import com.titammods.not_enough_sigils.setup.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class AscendedRunes {
    private static final Map<BlockEntity, Double> COST_MULTIPLIERS = Collections.synchronizedMap(new WeakHashMap<>());

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            IAltarRuneRegistry registry = NeoVitaeAPI.getInstance().getRuneRegistry();
            registry.registerRuneBlock(ModBlocks.RUNE_3_CAPACITY_AUGMENTED.get(), EnumAltarRuneType.CAPACITY, 3);
            registry.registerRuneBlock(ModBlocks.RUNE_3_SPEED.get(), EnumAltarRuneType.SPEED, 3);
            registry.registerRuneBlock(ModBlocks.RUNE_ETERNITY.get(), EnumAltarRuneType.EFFICIENCY, 3);
        });
    }

    public static void onCalculateStats(AltarRuneEvent.CalculateStats event) {
        int tier = event.getTier();
        AltarRuneModifiers modifiers = event.getModifiers();
        double reduction = 0.0D;
        double maxReduction = 0.0D;
        int acceleration = 0;
        for (RuneInstance instance : event.getRuneInstances()) {
            AscendedRuneStats stats = BuiltInRegistries.BLOCK.wrapAsHolder(instance.block()).getData(AscendedRuneStats.DATA_MAP);
            if (stats == null || tier < stats.minTier()) {
                continue;
            }
            if (stats.capacityMod() != 0.0D) {
                modifiers.addCapacityMod((float) stats.capacityMod());
            }
            if (stats.augmentedCapacityPower() != 1.0D) {
                modifiers.multiplyCapacityMod((float) stats.augmentedCapacityPower());
            }
            if (stats.consumptionMod() != 0.0D) {
                modifiers.addConsumptionMod((float) stats.consumptionMod());
            }
            acceleration += stats.accelerationMod();
            if (stats.costReduction() > 0.0D) {
                reduction += stats.costReduction();
                maxReduction = Math.max(maxReduction, stats.maxCostReduction());
            }
        }
        if (acceleration != 0) {
            modifiers.adjustTickRate(-acceleration);
        }
        BlockEntity tile = event.getLevel().getBlockEntity(event.getPos());
        if (tile != null) {
            double clamped = Math.max(0.0D, Math.min(Math.min(maxReduction, 0.95D), reduction));
            if (clamped > 0.0D) {
                COST_MULTIPLIERS.put(tile, 1.0D - clamped);
            } else {
                COST_MULTIPLIERS.remove(tile);
            }
        }
    }

    public static int applyCostReduction(BlockEntity tile, int required) {
        Double multiplier = COST_MULTIPLIERS.get(tile);
        if (multiplier == null || required <= 0) {
            return required;
        }
        return Math.max(1, (int) Math.ceil(required * multiplier));
    }
}
