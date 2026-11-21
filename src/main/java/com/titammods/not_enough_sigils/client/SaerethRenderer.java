package com.titammods.not_enough_sigils.client;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.saereth.SaerethEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SaerethRenderer extends HumanoidMobRenderer<SaerethEntity, SaerethModel> {
    private static final ResourceLocation TEXTURE = NotEnoughSigils.rl("textures/entity/saereth.png");

    public SaerethRenderer(EntityRendererProvider.Context context) {
        super(context, new SaerethModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(SaerethEntity entity) {
        return TEXTURE;
    }
}
