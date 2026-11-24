package com.titammods.not_enough_sigils.datagen;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.setup.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NotEnoughSigils.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (Block rune : ModBlocks.runes()) {
            glowingRune(rune);
        }
    }

    private void glowingRune(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        BlockModelBuilder model = models().getBuilder("block/" + name)
                .parent(new ModelFile.UncheckedModelFile(mcLoc("block/block")))
                .texture("base", modLoc("block/" + name))
                .texture("glow", modLoc("block/" + name + "_e"))
                .texture("particle", modLoc("block/" + name))
                .renderType("cutout");
        model.element()
                .from(0, 0, 0)
                .to(16, 16, 16)
                .allFaces((direction, face) -> face.texture("#base").cullface(direction))
                .end();
        model.element()
                .from(-0.005F, -0.005F, -0.005F)
                .to(16.005F, 16.005F, 16.005F)
                .allFaces((direction, face) -> face.texture("#glow"))
                .emissivity(15, 15)
                .end();
        simpleBlock(block, model);
    }
}
