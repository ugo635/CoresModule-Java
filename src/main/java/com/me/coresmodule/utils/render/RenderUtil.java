package com.me.coresmodule.utils.render;

import com.me.coresmodule.mixin.BeaconBlockEntityRendererInvoker;
import com.me.coresmodule.utils.math.CmVectors;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

import static com.me.coresmodule.CoresModule.mc;
import static java.lang.Math.max;

public class RenderUtil {
    public static void renderWaypoint(
            WorldRenderContext context,
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
            WorldRenderContext context,
            CmVectors pos,
            double width,
            double height,
            double depth,
            float[] colorComponents,
            float alpha,
            boolean throughWalls
    ) {
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();

        matrices.push();
        matrices.translate(pos.x + 0.5 - cameraPos.x, pos.y - cameraPos.y, pos.z + 0.5 - cameraPos.z);

        VertexConsumer buffer = context.consumers().getBuffer(
                throughWalls ? CmRenderLayers.FILLED_BOX_THROUGH_WALLS : CmRenderLayers.FILLED_BOX
        );

        float minX = (float)(-width / 2.0);
        float minY = 0f;
        float minZ = (float)(-depth / 2.0);
        float maxX = (float)(width / 2.0);
        float maxY = (float)height;
        float maxZ = (float)(depth / 2.0);

        VertexRendering.drawFilledBox(
                matrices, buffer,
                minX, minY, minZ,
                maxX, maxY, maxZ,
                colorComponents[0], colorComponents[1], colorComponents[2], alpha
        );

        matrices.pop();
    }

    /*
    public static void drawFilledBox(
            MatrixStack matrices,
            VertexConsumer buffer,
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ,
            float red, float green, float blue, float alpha
    ) {
        Matrix4f mat = matrices.peek().getPositionMatrix();

        // Helper to reduce repetition
        BiConsumer<float[], float[]> v = (p, n) -> {
            buffer.vertex(mat, p[0], p[1], p[2])
                    .color(red, green, blue, alpha)
                    .normal(n[0], n[1], n[2]);
        };

        // NORTH (0,0,-1)
        v.accept(new float[]{minX, minY, minZ}, new float[]{0,0,-1});
        v.accept(new float[]{maxX, minY, minZ}, new float[]{0,0,-1});
        v.accept(new float[]{maxX, maxY, minZ}, new float[]{0,0,-1});
        v.accept(new float[]{minX, maxY, minZ}, new float[]{0,0,-1});

        // SOUTH (0,0,1)
        v.accept(new float[]{minX, minY, maxZ}, new float[]{0,0,1});
        v.accept(new float[]{minX, maxY, maxZ}, new float[]{0,0,1});
        v.accept(new float[]{maxX, maxY, maxZ}, new float[]{0,0,1});
        v.accept(new float[]{maxX, minY, maxZ}, new float[]{0,0,1});

        // WEST (-1,0,0)
        v.accept(new float[]{minX, minY, minZ}, new float[]{-1,0,0});
        v.accept(new float[]{minX, maxY, minZ}, new float[]{-1,0,0});
        v.accept(new float[]{minX, maxY, maxZ}, new float[]{-1,0,0});
        v.accept(new float[]{minX, minY, maxZ}, new float[]{-1,0,0});

        // EAST (1,0,0)
        v.accept(new float[]{maxX, minY, minZ}, new float[]{1,0,0});
        v.accept(new float[]{maxX, minY, maxZ}, new float[]{1,0,0});
        v.accept(new float[]{maxX, maxY, maxZ}, new float[]{1,0,0});
        v.accept(new float[]{maxX, maxY, minZ}, new float[]{1,0,0});

        // UP (0,1,0)
        v.accept(new float[]{minX, maxY, minZ}, new float[]{0,1,0});
        v.accept(new float[]{maxX, maxY, minZ}, new float[]{0,1,0});
        v.accept(new float[]{maxX, maxY, maxZ}, new float[]{0,1,0});
        v.accept(new float[]{minX, maxY, maxZ}, new float[]{0,1,0});

        // DOWN (0,-1,0)
        v.accept(new float[]{minX, minY, minZ}, new float[]{0,-1,0});
        v.accept(new float[]{minX, minY, maxZ}, new float[]{0,-1,0});
        v.accept(new float[]{maxX, minY, maxZ}, new float[]{0,-1,0});
        v.accept(new float[]{maxX, minY, minZ}, new float[]{0,-1,0});
    }
    */



    public static void drawString(
            WorldRenderContext context,
            CmVectors pos,
            double yOffset,
            String text,
            int color,
            boolean shadow,
            double scale,
            boolean throughWalls
    ) {
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        float cameraYaw = context.camera().getYaw();
        float cameraPitch = context.camera().getPitch();
        TextRenderer textRenderer = mc.textRenderer;

        matrices.push();

        Vec3d textWorldPos = new Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
        double distance = cameraPos.distanceTo(textWorldPos);
        double dynamicScale = max(distance, 2.5) * scale;

        matrices.translate(pos.x + 0.5 - cameraPos.x, pos.y + yOffset - cameraPos.y, pos.z + 0.5 - cameraPos.z);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cameraPitch));

        matrices.scale((float)-dynamicScale, (float)-dynamicScale, (float)dynamicScale);

        int textWidth = textRenderer.getWidth(text);
        float xOffset = -textWidth / 2f;

        TextRenderer.TextLayerType layerType = throughWalls ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL;

        textRenderer.draw(
                text,
                xOffset,
                0f,
                color,
                shadow,
                matrices.peek().getPositionMatrix(),
                context.consumers(),
                layerType,
                0,
                0xF000F0
        );

        matrices.pop();
    }

    public static void renderBeaconBeam(
            WorldRenderContext context,
            CmVectors pos,
            int yOffset,
            float[] colorComponents
    ) {
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        ClientWorld world = context.world();

        matrices.push();
        matrices.translate(pos.x - cameraPos.x, pos.y - cameraPos.y, pos.z - cameraPos.z);

        VertexConsumerProvider consumers = context.consumers();
        float partialTicks = context.tickCounter().getTickProgress(true);
        long worldAge = world.getTime();

        int beamHeight = context.world().getHeight();
        float[] beamColor = new float[] {colorComponents[0], colorComponents[1], colorComponents[2], 1.0f};

        BeaconBlockEntityRendererInvoker.renderBeam(
                matrices,
                consumers,
                partialTicks,
                1.0f,
                worldAge,
                yOffset,
                beamHeight,
                new Color(beamColor[0], beamColor[1], beamColor[2]).getRGB()
        );

        // TODO: Add through walls option
        matrices.pop();
    }

    public static void drawLineFromCursor(
            WorldRenderContext context,
            CmVectors target,
            float[] color,
            float lineWidth,
            boolean throughWalls,
            float alpha
    ) {
        if (alpha == 0) alpha = 0.5f;

        var camera = context.camera();
        var cameraPos = camera.getPos();
        var matrices = context.matrixStack();

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer buffer = context.consumers().getBuffer(CmRenderLayers.getLines(lineWidth, throughWalls));
        Vec3d startPos = cameraPos.add(Vec3d.fromPolar(camera.getPitch(), camera.getYaw()));
        Vec3d endPos = target.center().toVec3d().add(0.0, 0.5, 0.0);

        Vec3d lineDir = endPos.subtract(startPos);
        Vec3d viewDir = startPos.subtract(cameraPos);
        Vec3d sideVec = lineDir.crossProduct(viewDir).normalize();
        Vec3d upVec = sideVec.crossProduct(lineDir).normalize();

        float nx = (float) upVec.x;
        float ny = (float) upVec.y;
        float nz = (float) upVec.z;

        MatrixStack.Entry entry = matrices.peek();

        buffer.vertex(entry.getPositionMatrix(), (float) startPos.x, (float) startPos.y, (float) startPos.z)
                .normal(entry, nx, ny, nz)
                .color(color[0], color[1], color[2], alpha);

        buffer.vertex(entry.getPositionMatrix(), (float) endPos.x, (float) endPos.y, (float) endPos.z)
                .normal(entry, nx, ny, nz)
                .color(color[0], color[1], color[2], alpha);

        matrices.pop();
    }



}
