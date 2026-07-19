package com.me.coresmodule.utils.render;

import com.me.coresmodule.mixin.BeaconBlockEntityRendererInvoker;
import com.me.coresmodule.utils.math.CmVectors;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.GizmoStyle;

import java.awt.*;

import static com.me.coresmodule.CoresModule.mc;
import static java.lang.Math.max;

public class RenderUtil {

    public static void renderWaypoint(
            LevelRenderContext context,
            String text,
            CmVectors pos,
            float[] colorComponents,
            int hexColor,
            float alpha,
            boolean throughWalls,
            boolean drawLine,
            boolean renderBeam,
            float lineWidth,
            float yPlus
    ) {
        drawFilledBox(context, pos, 1.0f, 1.0f, 1.0f, colorComponents, alpha, throughWalls);

        if (drawLine) {
            pos.y += yPlus;
            drawLineFromCursor(context, pos, colorComponents, lineWidth, throughWalls, alpha);
        }

        if (renderBeam) {
            renderBeaconBeam(context, pos, 1, colorComponents);
        }

        if (text != null && !text.isEmpty() && !text.equals("§7")) {
            drawString(context, pos, 1.5f, text, hexColor, true, 0.01f, throughWalls);
        }
    }

    public static void drawFilledBox(
            LevelRenderContext context,
            CmVectors pos,
            double width,
            double height,
            double depth,
            float[] colorComponents,
            float alpha,
            boolean throughWalls
    ) {
        int r = (int) (Math.clamp(colorComponents[0], 0f, 1f) * 255);
        int g = (int) (Math.clamp(colorComponents[1], 0f, 1f) * 255);
        int b = (int) (Math.clamp(colorComponents[2], 0f, 1f) * 255);
        int a = (int) (Math.clamp(alpha, 0f, 1f) * 255);

        int argbColor = (a << 24) | (r << 16) | (g << 8) | b;

        BlockPos bPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);

        Gizmos.cuboid(
                AABB.ofSize(
                        Vec3.atCenterOf(bPos),
                        width,
                        height,
                        depth
                ),
                GizmoStyle.fill(argbColor)
        );
    }

    public static void drawString(
            LevelRenderContext context,
            CmVectors pos,
            double yOffset,
            String text,
            int color,
            boolean shadow,
            double scale,
            boolean throughWalls
    ) {
        PoseStack matrices = context.poseStack();

        Camera camera = getCamera();
        Vec3 cameraPos = camera.position();

        float cameraYaw = camera.yRot();
        float cameraPitch = camera.xRot();

        Font textRenderer = mc.font;

        matrices.pushPose();

        Vec3 textWorldPos = new Vec3(
                pos.x + 0.5,
                pos.y + 0.5,
                pos.z + 0.5
        );

        double distance = cameraPos.distanceTo(textWorldPos);
        double dynamicScale = max(distance, 2.5) * scale;

        matrices.translate(
                pos.x + 0.5 - cameraPos.x,
                pos.y + yOffset - cameraPos.y,
                pos.z + 0.5 - cameraPos.z
        );

        matrices.mulPose(Axis.YP.rotationDegrees(-cameraYaw));
        matrices.mulPose(Axis.XP.rotationDegrees(cameraPitch));

        matrices.scale(
                (float) -dynamicScale,
                (float) -dynamicScale,
                (float) dynamicScale
        );

        int textWidth = textRenderer.width(text);
        float xOffset = -textWidth / 2f;

        Font.DisplayMode displayMode = throughWalls
                ? Font.DisplayMode.SEE_THROUGH
                : Font.DisplayMode.NORMAL;

        textRenderer.drawInBatch(
                text,
                xOffset,
                0f,
                color,
                shadow,
                matrices.last().pose(),
                context.bufferSource(),
                displayMode,
                0,
                0xF000F0
        );

        matrices.popPose();
    }

    public static void renderBeaconBeam(
            LevelRenderContext context,
            CmVectors pos,
            int yOffset,
            float[] colorComponents
    ) {
        PoseStack matrices = context.poseStack();

        Vec3 cameraPos = getCamera().position();
        ClientLevel world = mc.level;

        matrices.pushPose();

        matrices.translate(
                pos.x - cameraPos.x,
                pos.y - cameraPos.y,
                pos.z - cameraPos.z
        );

        float partialTicks = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);

        if (world == null) return;
        int beamHeight = world.getHeight();

        float[] beamColor = {
                colorComponents[0],
                colorComponents[1],
                colorComponents[2],
                1.0f
        };

        SubmitNodeCollector queue = context.gameRenderer().getSubmitNodeStorage();

        BeaconBlockEntityRendererInvoker.renderBeam(
                matrices,
                queue,
                partialTicks,
                1.0f,
                yOffset,
                beamHeight,
                new Color(
                        beamColor[0],
                        beamColor[1],
                        beamColor[2]
                ).getRGB()
        );

        matrices.popPose();
    }

    public static void drawLineFromCursor(
            LevelRenderContext context,
            CmVectors target,
            float[] color,
            float lineWidth,
            boolean throughWalls,
            float alpha
    ) {
        if (alpha == 0) {
            alpha = 0.5f;
        }

        Camera camera = getCamera();
        Vec3 cameraPos = camera.position();

        Vec3 startPos = cameraPos.add(
                Vec3.directionFromRotation(
                        camera.xRot(),
                        camera.yRot()
                )
        );

        Vec3 endPos = target.center()
                .toVec3()
                .add(0.0, 0.5, 0.0);

        int r = (int) (Math.clamp(color[0], 0f, 1f) * 255);
        int g = (int) (Math.clamp(color[1], 0f, 1f) * 255);
        int b = (int) (Math.clamp(color[2], 0f, 1f) * 255);
        int a = (int) (Math.clamp(alpha, 0f, 1f) * 255);
        int argbColor = (a << 24) | (r << 16) | (g << 8) | b;

        Gizmos.line(startPos, endPos, argbColor, lineWidth);
    }

    private static Camera getCamera() {
        return mc.gameRenderer.getMainCamera();
    }
}