package com.titammods.not_enough_sigils.datagen;

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
    protected void gather(HolderLookup.Provider provider) {
        builder(NVDataMaps.SPIRITUS_GEM_MAX_AMOUNTS)
                .add(ModItems.SPIRITUS_GEM_EXALTED.getKey(), 32768.0D, false)
                .add(ModItems.SPIRITUS_GEM_PRIMORDIAL.getKey(), 65536.0D, false)
                .build();
    }
}
