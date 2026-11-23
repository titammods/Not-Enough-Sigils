package com.titammods.not_enough_sigils.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.titammods.not_enough_sigils.entity.MagicMissileEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class MagicMissileRenderer extends EntityRenderer<MagicMissileEntity, EntityRenderState> {

    public MagicMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
    }
}
