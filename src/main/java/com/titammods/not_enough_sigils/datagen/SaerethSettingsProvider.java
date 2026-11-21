package com.titammods.not_enough_sigils.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.titammods.not_enough_sigils.saereth.SaerethSettings;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class SaerethSettingsProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public SaerethSettingsProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, SaerethSettings.DIRECTORY);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        JsonElement json = SaerethSettings.CODEC.encodeStart(JsonOps.INSTANCE, SaerethSettings.DEFAULT).getOrThrow();
        return DataProvider.saveStable(output, json, pathProvider.json(SaerethSettings.FILE));
    }

    @Override
    public String getName() {
        return "Saereth Settings";
    }
}
