package com.me.coresmodule.utils.render;

import com.me.coresmodule.CoresModule;
import com.me.coresmodule.utils.math.CmVectors;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

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
        float x1, y1, z1, x2, y2, z2;
        x1 = (float) pos.x;
        y1 = (float) pos.y;
        z1 = (float) pos.z;

        x2 = x1 + 1;
        y2 = y1 + 1;
        z2 = z1 + 1;

        float r, g, b;
        r = colorComponents[0];
        g = colorComponents[1];
        b = colorComponents[2];

        MatrixStack matrix = context.matrixStack();
        VertexConsumer vertex = context.consumers().getBuffer(RenderLayer.getLines());

        VertexRendering.drawFilledBox(matrix, vertex, x1, y1, z1, x2, y2, z2, r, g, b, alpha);
    }
}
