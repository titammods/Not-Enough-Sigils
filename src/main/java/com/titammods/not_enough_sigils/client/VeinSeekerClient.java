package com.titammods.not_enough_sigils.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.titammods.not_enough_sigils.network.VeinSeekerPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;
import java.util.OptionalDouble;

public class VeinSeekerClient {
    private static final int FALLBACK_COLOR = 0xFFD24A;

    private static final RenderType ORE_OUTLINE = RenderType.create(
            "not_enough_sigils:vein_seeker",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(2.0D)))
                    .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderType.MAIN_TARGET)
                    .setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
                    .setCullState(RenderType.NO_CULL)
                    .setDepthTestState(RenderType.NO_DEPTH_TEST)
                    .createCompositeState(false));

    private static List<BlockPos> positions = List.of();
    private static long expiresAt;

    public static void receive(VeinSeekerPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        positions = List.copyOf(payload.positions());
        expiresAt = minecraft.level.getGameTime() + payload.duration();
    }

    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || positions.isEmpty()) {
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

        Vec3 camera = event.getCamera().getPosition();
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
            LevelRenderer.renderLineBox(poseStack, consumer, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, red, green, blue, 1.0F);
            poseStack.popPose();
        }

        RenderSystem.disableDepthTest();
        buffers.endBatch(ORE_OUTLINE);
        RenderSystem.enableDepthTest();
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        positions = List.of();
    }
}
