package com.titammods.not_enough_sigils.altar;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public record AscendedRuneStats(int minTier, double capacityMod, double augmentedCapacityPower, double consumptionMod, int accelerationMod,
                                double costReduction, double maxCostReduction) {
    public static final int DEFAULT_MIN_TIER = 6;

    public static final Codec<AscendedRuneStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("min_tier", DEFAULT_MIN_TIER).forGetter(AscendedRuneStats::minTier),
            Codec.DOUBLE.optionalFieldOf("capacity_mod", 0.0D).forGetter(AscendedRuneStats::capacityMod),
            Codec.DOUBLE.optionalFieldOf("augmented_capacity_power", 1.0D).forGetter(AscendedRuneStats::augmentedCapacityPower),
            Codec.DOUBLE.optionalFieldOf("consumption_mod", 0.0D).forGetter(AscendedRuneStats::consumptionMod),
            Codec.INT.optionalFieldOf("acceleration_mod", 0).forGetter(AscendedRuneStats::accelerationMod),
            Codec.DOUBLE.optionalFieldOf("cost_reduction", 0.0D).forGetter(AscendedRuneStats::costReduction),
            Codec.DOUBLE.optionalFieldOf("max_cost_reduction", 0.5D).forGetter(AscendedRuneStats::maxCostReduction)
    ).apply(instance, AscendedRuneStats::new));

    public static final DataMapType<Block, AscendedRuneStats> DATA_MAP = DataMapType.builder(
            NotEnoughSigils.rl("ascended_rune_stats"), Registries.BLOCK, CODEC
    ).synced(CODEC, true).build();

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(DATA_MAP);
    }

    public static AscendedRuneStats augmentedCapacity(double power) {
        return new AscendedRuneStats(DEFAULT_MIN_TIER, 0.0D, power, 0.0D, 0, 0.0D, 0.5D);
    }

    public static AscendedRuneStats speed(double amount, int acceleration) {
        return new AscendedRuneStats(DEFAULT_MIN_TIER, 0.0D, 1.0D, amount, acceleration, 0.0D, 0.5D);
    }

    public static AscendedRuneStats eternity(double reduction, double max) {
        return new AscendedRuneStats(DEFAULT_MIN_TIER, 0.0D, 1.0D, 0.0D, 0, reduction, max);
    }
}
