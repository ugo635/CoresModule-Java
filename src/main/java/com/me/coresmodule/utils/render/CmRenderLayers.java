package com.me.coresmodule.utils.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import java.util.OptionalDouble;

import static com.me.coresmodule.CoresModule.MOD_ID;

public class CmRenderLayers {

    public static final RenderLayer.MultiPhase FILLED_BOX = RenderLayer.of(
            "filled_box",
            RenderLayer.DEFAULT_BUFFER_SIZE,
            false,
            true,
            RenderPipelines.DEBUG_FILLED_BOX,
            RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false)
    );

    public static final RenderLayer.MultiPhase FILLED_BOX_THROUGH_WALLS = RenderLayer.of(
            "filled_box_through_walls",
            RenderLayer.DEFAULT_BUFFER_SIZE,
            false,
            true,
            CmRenderPipelines.FILLED_BOX_THROUGH_WALLS,
            RenderLayer.MultiPhaseParameters.builder()
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .build(false)
    );

    public static RenderLayer getLines(double lineWidth, boolean throughWalls) {
        if (throughWalls) {
            return RenderLayer.of(
                    "lines_through_walls",
                    RenderLayer.DEFAULT_BUFFER_SIZE,
                    false,
                    true,
                    CmRenderPipelines.LINES_THROUGH_WALLS,
                    RenderLayer.MultiPhaseParameters.builder()
                            .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                            .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(lineWidth)))
                            .build(false)
            );
        } else {
            return RenderLayer.of(
                    "lines",
                    RenderLayer.DEFAULT_BUFFER_SIZE,
                    false,
                    true,
                    CmRenderPipelines.LINES,
                    RenderLayer.MultiPhaseParameters.builder()
                            .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                            .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(lineWidth)))
                            .build(false)
            );
        }
    }
}

class CmRenderPipelines {

    public static final RenderPipeline FILLED_BOX_THROUGH_WALLS = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation(Identifier.of(MOD_ID, "pipeline/debug_filled_box_through_walls"))
                    .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .build()
    );

    public static final RenderPipeline LINES = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
                    .withLocation(Identifier.of(MOD_ID, "pipeline/line_strip"))
                    .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, DrawMode.LINES)
                    .withCull(false)
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withDepthWrite(true)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .build()
    );

    public static final RenderPipeline LINES_THROUGH_WALLS = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
                    .withLocation(Identifier.of(MOD_ID, "pipeline/line_through_walls"))
                    .withShaderDefine("shad")
                    .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, DrawMode.LINES)
                    .withCull(false)
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withDepthWrite(false)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .build()
    );
}