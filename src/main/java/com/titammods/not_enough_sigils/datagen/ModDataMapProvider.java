package com.titammods.not_enough_sigils.datagen;

import com.breakinblocks.neovitae.common.datamap.BloodOrb;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.titammods.not_enough_sigils.setup.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {

    public ModDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void gather() {
        builder(NVDataMaps.SPIRITUS_GEM_MAX_AMOUNTS)
                .add(ModItems.SPIRITUS_GEM_EXALTED, 32768.0D, false)
                .add(ModItems.SPIRITUS_GEM_PRIMORDIAL, 65536.0D, false);
        builder(NVDataMaps.BLOOD_ORB_STATS)
                .add(ModItems.BLOOD_ORB_ASCENDANT, new BloodOrb(6, 20000, 75000000, 400), false)
                .add(ModItems.BLOOD_ORB_DIVINE, new BloodOrb(7, 24000, 200000000, 650), false);
    }
}
