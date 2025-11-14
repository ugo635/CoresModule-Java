package com.me.coresmodule.utils.render;

import com.me.coresmodule.CoresModule;
import com.me.coresmodule.utils.math.CmVectors;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.function.Consumers;

import java.util.Objects;

import static com.me.coresmodule.CoresModule.mc;

public class RenderUtil {
    private static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
            .withLocation(Identifier.of(CoresModule.MOD_ID, "pipeline/debug_filled_box_through_walls"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build()
    );

    public static void renderWaypoint(
            WorldRenderContext context,
            String text,
            CmVectors pos,
            Float[] colorComponents,
            int hexColor,
            float alpha,
            boolean throughWalls,
            boolean drawLine,
            boolean renderBeam,
            float lineWidth
    ) {
        drawFilledBox(context, pos, 1.0f, 1.0f, 1.0f, colorComponents, alpha, throughWalls);

        if (drawLine) {
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
            float width,
            float height,
            float depth,
            Float[] colorComponents,
            float alpha,
            boolean throughWalls
    ) {
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        matrices.push();
        matrices.translate(pos.x + 0.5 - cameraPos.x, pos.y - cameraPos.y, pos.z + 0.5 - cameraPos.z);

        VertexConsumer buffer = context.consumers().getBuffer(RenderLayer.getLines()); // Placeholder layer
        float minX = -width / 2f;
        float minY = 0f;
        float minZ = -depth / 2f;
        float maxX = width / 2f;
        float maxY = height;
        float maxZ = depth / 2f;

        VertexRendering.drawFilledBox(
                matrices,
                buffer,
                minX, minY, minZ,
                maxX, maxY, maxZ,
                colorComponents[0], colorComponents[1], colorComponents[2], alpha
        );

        matrices.pop();
    }

    public static void drawString(
            WorldRenderContext context,
            CmVectors pos,
            float yOffset,
            String text,
            int color,
            boolean shadow,
            float scale,
            boolean throughWalls
    ) {
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        float cameraYaw = context.camera().getYaw();
        float cameraPitch = context.camera().getPitch();
        TextRenderer textRenderer = mc.textRenderer;

        matrices.push();
        matrices.translate(pos.x + 0.5 - cameraPos.x, pos.y + yOffset - cameraPos.y, pos.z + 0.5 - cameraPos.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cameraPitch));
        matrices.scale(-scale, -scale, scale);

        float textWidth = textRenderer.getWidth(text) / 2f;

        textRenderer.draw(
                text,
                -textWidth,
                0f,
                color,
                shadow,
                matrices.peek().getPositionMatrix(),
                context.consumers(),
                throughWalls ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL,
                0,
                0xF000F0
        );

        matrices.pop();
    }

    public static void renderBeaconBeam(
            WorldRenderContext context,
            CmVectors pos,
            int yOffset,
            Float[] colorComponents
    ) {
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();

        matrices.push();
        matrices.translate(pos.x - cameraPos.x, pos.y - cameraPos.y, pos.z - cameraPos.z);

        // TODO: Add through walls option
        matrices.pop();
    }

    public static void drawLineFromCursor(
            WorldRenderContext context,
            CmVectors target,
            Float[] color,
            float lineWidth,
            boolean throughWalls,
            float alpha
    ) {
        Vec3d cameraPos = context.camera().getPos();
        MatrixStack matrices = context.matrixStack();
        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer buffer = context.consumers().getBuffer(RenderLayer.getLines());
        Vec3d startPos = cameraPos;
        Vec3d endPos = target.center().toVec3d().add(0.0, 0.5, 0.0);

        Consumers consumers = (Consumers) Objects.requireNonNull(context.consumers());

        Vec3d lineDir = endPos.subtract(startPos);
        Vec3d viewDir = startPos.subtract(cameraPos);

        Vec3d sideVec = lineDir.crossProduct(viewDir).normalize();

        Vec3d upVec = sideVec.crossProduct(lineDir).normalize();

        float nx = (float) upVec.x;
        float ny = (float) upVec.y;
        float nz = (float) upVec.z;


        buffer.vertex(matrices.peek().getPositionMatrix(), (float) startPos.x, (float) startPos.y, (float) startPos.z)
                .normal(matrices.peek(), nx, ny, nz)
                .color(color[0], color[1], color[2], alpha);

        buffer.vertex(matrices.peek().getPositionMatrix(), (float) endPos.x, (float) endPos.y, (float) endPos.z)
                .normal(matrices.peek(), nx, ny, nz)
                .color(color[0], color[1], color[2], alpha);

        matrices.pop();
    }

}
