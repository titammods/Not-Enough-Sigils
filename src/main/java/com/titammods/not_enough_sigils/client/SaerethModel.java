package com.titammods.not_enough_sigils.client;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.saereth.SaerethEntity;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;

public class SaerethModel extends PlayerModel {
    public static final ContextKey<SaerethEntity.Action> ACTION = new ContextKey<>(NotEnoughSigils.rl("saereth_action"));

    public SaerethModel(ModelPart root) {
        super(root, false);
    }

    @Override
    public void setupAnim(AvatarRenderState state) {
        super.setupAnim(state);
        SaerethEntity.Action action = state.getRenderData(ACTION);
        if (action == null) {
            return;
        }
        float age = state.ageInTicks;
        switch (action) {
            case DRAW -> draw(age);
            case PLACE -> place(age);
            case CAST -> cast(age);
            default -> {
            }
        }
    }

    private void draw(float age) {
        float sweep = Mth.sin(age * 0.45F);
        rightArm.xRot = -1.1F + Mth.abs(sweep) * 0.3F;
        rightArm.yRot = sweep * 0.6F;
        rightArm.zRot = 0.0F;
        leftArm.xRot = -0.6F;
        leftArm.zRot = -0.2F;
        head.xRot = 0.6F;
    }

    private void place(float age) {
        float reach = Mth.sin(age * 0.3F) * 0.15F;
        rightArm.xRot = -1.25F + reach;
        rightArm.yRot = -0.15F;
        leftArm.xRot = -0.9F - reach;
        leftArm.yRot = 0.2F;
        head.xRot = 0.35F;
    }

    private void cast(float age) {
        float pulse = Mth.sin(age * 0.25F) * 0.12F;
        rightArm.xRot = -2.7F + pulse;
        rightArm.zRot = -0.25F - pulse;
        rightArm.yRot = 0.0F;
        leftArm.xRot = -2.7F - pulse;
        leftArm.zRot = 0.25F + pulse;
        leftArm.yRot = 0.0F;
        head.xRot = -0.25F;
    }
}
