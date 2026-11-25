package com.titammods.not_enough_sigils.client;

import com.titammods.not_enough_sigils.saereth.SaerethEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class SaerethModel extends PlayerModel<SaerethEntity> {

    public SaerethModel(ModelPart root) {
        super(root, false);
    }

    @Override
    public void setupAnim(SaerethEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        SaerethEntity.Action action = entity.getAction();
        crouching = action == SaerethEntity.Action.DRAW;
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        switch (action) {
            case DRAW -> draw(ageInTicks);
            case PLACE -> place(ageInTicks);
            case CAST -> cast(ageInTicks);
            case LIBRARY -> library(ageInTicks);
            default -> {
            }
        }
        hat.copyFrom(head);
        jacket.copyFrom(body);
        leftSleeve.copyFrom(leftArm);
        rightSleeve.copyFrom(rightArm);
        leftPants.copyFrom(leftLeg);
        rightPants.copyFrom(rightLeg);
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

    private void library(float age) {
        float weave = Mth.sin(age * 0.16F);
        float lift = Mth.cos(age * 0.16F);
        rightArm.xRot = -2.25F + weave * 0.25F;
        rightArm.yRot = -0.35F + lift * 0.3F;
        rightArm.zRot = -0.35F - weave * 0.2F;
        leftArm.xRot = -2.25F - weave * 0.25F;
        leftArm.yRot = 0.35F - lift * 0.3F;
        leftArm.zRot = 0.35F + weave * 0.2F;
        head.xRot = -0.55F + Mth.sin(age * 0.08F) * 0.05F;
        body.xRot = -0.05F;
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
