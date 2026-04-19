package com.me.coresmodule.utils.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.LayeringTransform;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import java.util.OptionalDouble;

import static com.me.coresmodule.CoresModule.MOD_ID;

public class CmRenderLayers {

    public static final RenderLayer FILLED_BOX = RenderLayer.of(
            "filled_box",
            RenderSetup.builder(RenderPipelines.DEBUG_FILLED_BOX)
                     .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                     .translucent()
                     .build()
    );

    public static final RenderLayer FILLED_BOX_THROUGH_WALLS = RenderLayer.of(
            "filled_box_through_walls",
            RenderSetup.builder(CmRenderPipelines.FILLED_BOX_THROUGH_WALLS)
                    .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .translucent()
                    .build()
    );

    public static RenderLayer getLines(double lineWidth, boolean throughWalls) {
        if (throughWalls) {
            return RenderLayer.of(
                    "lines_through_walls",
                    RenderSetup.builder(CmRenderPipelines.LINES)
                             .translucent()
                             .build()
            );
        } else {
            return RenderLayer.of(
                    "lines",
                    RenderSetup.builder(CmRenderPipelines.LINES_THROUGH_WALLS)
                            .translucent()
                            .build()
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