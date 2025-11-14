package com.me.coresmodule.utils.render;

import com.me.coresmodule.utils.math.CmVectors;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.function.BiConsumer;

import static com.me.coresmodule.CoresModule.MOD_ID;
import static com.me.coresmodule.CoresModule.mc;

public class RenderUtil {
    private static final RenderPipeline FILLED_BOX_THROUGH_WALLS_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET) // snippet that exists in your mappings
                    .withLocation(Identifier.of(MOD_ID, "pipeline/debug_filled_box_through_walls"))
                    // IMPORTANT: use a vertex format that contains normals because drawFilledBox emits normals
                    .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, VertexFormat.DrawMode.TRIANGLE_STRIP)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .build()
    );

    // 2) Create a RenderLayer that uses that pipeline so getBuffer(...) accepts it
    private static final RenderLayer FILLED_BOX_THROUGH_WALLS = RenderLayer.of(
            "debug_filled_box_through_walls",
            RenderLayer.DEFAULT_BUFFER_SIZE,
            false,
            true,
            FILLED_BOX_THROUGH_WALLS_PIPELINE,
            RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false)
    );

    // Example existing filled-box RenderLayer (leave as-is)
    private static final RenderLayer FILLED_BOX = RenderLayer.of(
            "filled_box",
            RenderLayer.DEFAULT_BUFFER_SIZE,
            false,
            true,
            RenderPipelines.DEBUG_FILLED_BOX,
            RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false)
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

        VertexConsumer buffer = (throughWalls)
                ? context.consumers().getBuffer(FILLED_BOX_THROUGH_WALLS)
                : context.consumers().getBuffer(FILLED_BOX); // TODO: Buffer.vertex().normal()


        float minX = -width / 2f;
        float minY = 0f;
        float minZ = -depth / 2f;
        float maxX = width / 2f;
        float maxY = height;
        float maxZ = depth / 2f;

        drawFilledBox(
                matrices,
                buffer,
                minX, minY, minZ,
                maxX, maxY, maxZ,
                colorComponents[0], colorComponents[1], colorComponents[2], alpha
        );

        matrices.pop();
    }

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

        Vec3d lineDir = endPos.subtract(startPos);
        Vec3d viewDir = startPos.subtract(cameraPos);

        Vec3d sideVec = lineDir.crossProduct(viewDir).normalize();
        Vec3d upVec = sideVec.crossProduct(lineDir).normalize();

        float nx = (float) upVec.x;
        float ny = (float) upVec.y;
        float nz = (float) upVec.z;

        MatrixStack.Entry entry = matrices.peek();

        buffer.vertex(entry.getPositionMatrix(), (float) startPos.x, (float) startPos.y, (float) startPos.z)
                .color(color[0], color[1], color[2], alpha)
                .normal(entry, nx, ny, nz);

        buffer.vertex(entry.getPositionMatrix(), (float) endPos.x, (float) endPos.y, (float) endPos.z)
                .color(color[0], color[1], color[2], alpha)
                .normal(entry, nx, ny, nz);

        matrices.pop();
    }


}
