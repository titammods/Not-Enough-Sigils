package com.titammods.not_enough_sigils.client;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.network.VeinSeekerPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.List;

public class VeinSeekerClient {
    private static final int FALLBACK_COLOR = 0xFFD24A;
    private static final float LINE_WIDTH = 2.0F;
    private static final float EDGE_OFFSET = 0.002F;

    private static final RenderPipeline ORE_OUTLINE_PIPELINE = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(NotEnoughSigils.rl("pipeline/vein_seeker"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build();

    private static final RenderType ORE_OUTLINE = RenderType.create(
            "not_enough_sigils_vein_seeker",
            RenderSetup.builder(ORE_OUTLINE_PIPELINE)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .createRenderSetup());

    private static List<BlockPos> positions = List.of();
    private static long expiresAt;

    public static void registerPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(ORE_OUTLINE_PIPELINE);
    }

    public static void receive(VeinSeekerPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        positions = List.copyOf(payload.positions());
        expiresAt = minecraft.level.getGameTime() + payload.duration();
    }

    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentParticles event) {
        if (positions.isEmpty()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        PoseStack poseStack = event.getPoseStack();
        if (minecraft.level == null || poseStack == null) {
            return;
        }
        if (minecraft.level.getGameTime() >= expiresAt) {
            positions = List.of();
            return;
        }

        Vec3 camera = event.getLevelRenderState().cameraRenderState.pos;
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        VertexConsumer consumer = buffers.getBuffer(ORE_OUTLINE);

        for (BlockPos pos : positions) {
            BlockState state = minecraft.level.getBlockState(pos);
            if (state.isAir()) {
                continue;
            }
            int color = state.getMapColor(minecraft.level, pos).col;
            if (color == MapColor.NONE.col) {
                color = FALLBACK_COLOR;
            }
            float red = ((color >> 16) & 0xFF) / 255.0F;
            float green = ((color >> 8) & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;

            poseStack.pushPose();
            poseStack.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
            box(consumer, poseStack.last().pose(), red, green, blue);
            poseStack.popPose();
        }

        buffers.endBatch(ORE_OUTLINE);
    }

    private static void box(VertexConsumer consumer, Matrix4f matrix, float red, float green, float blue) {
        float min = -EDGE_OFFSET;
        float max = 1.0F + EDGE_OFFSET;

        line(consumer, matrix, min, min, min, max, min, min, red, green, blue);
        line(consumer, matrix, max, min, min, max, min, max, red, green, blue);
        line(consumer, matrix, max, min, max, min, min, max, red, green, blue);
        line(consumer, matrix, min, min, max, min, min, min, red, green, blue);

        line(consumer, matrix, min, max, min, max, max, min, red, green, blue);
        line(consumer, matrix, max, max, min, max, max, max, red, green, blue);
        line(consumer, matrix, max, max, max, min, max, max, red, green, blue);
        line(consumer, matrix, min, max, max, min, max, min, red, green, blue);

        line(consumer, matrix, min, min, min, min, max, min, red, green, blue);
        line(consumer, matrix, max, min, min, max, max, min, red, green, blue);
        line(consumer, matrix, max, min, max, max, max, max, red, green, blue);
        line(consumer, matrix, min, min, max, min, max, max, red, green, blue);
    }

    private static void line(VertexConsumer consumer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2,
                             float red, float green, float blue) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length > 0.0F) {
            dx /= length;
            dy /= length;
            dz /= length;
        }
        consumer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, 1.0F).setNormal(dx, dy, dz).setLineWidth(LINE_WIDTH);
        consumer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, 1.0F).setNormal(dx, dy, dz).setLineWidth(LINE_WIDTH);
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        positions = List.of();
    }
}
