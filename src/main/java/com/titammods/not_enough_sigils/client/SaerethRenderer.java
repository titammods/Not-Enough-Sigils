package com.titammods.not_enough_sigils.client;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.saereth.SaerethEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;

public class SaerethRenderer extends HumanoidMobRenderer<SaerethEntity, AvatarRenderState, SaerethModel> {
    private static final Identifier TEXTURE = NotEnoughSigils.rl("textures/entity/saereth.png");

    public SaerethRenderer(EntityRendererProvider.Context context) {
        super(context, new SaerethModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public AvatarRenderState createRenderState() {
        return new AvatarRenderState();
    }

    @Override
    public void extractRenderState(SaerethEntity entity, AvatarRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        SaerethEntity.Action action = entity.getAction();
        state.setRenderData(SaerethModel.ACTION, action);
        state.isCrouching = action == SaerethEntity.Action.DRAW;
    }

    @Override
    public Identifier getTextureLocation(AvatarRenderState state) {
        return TEXTURE;
    }
}
