package com.me.coresmodule.utils.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.Identifier;

import static com.me.coresmodule.CoresModule.MOD_ID;

public class CmRenderLayers {

    public static final RenderType FILLED_BOX = RenderType.create(
            "filled_box",
            RenderSetup.builder(RenderPipelines.DEBUG_FILLED_BOX)
                     .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                     .sortOnUpload()
                     .createRenderSetup()
    );

    public static final RenderType FILLED_BOX_THROUGH_WALLS = RenderType.create(
            "filled_box_through_walls",
            RenderSetup.builder(CmRenderPipelines.FILLED_BOX_THROUGH_WALLS)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .sortOnUpload()
                    .createRenderSetup()
    );

    public static final RenderType LINES = RenderType.create(
            "lines",
            RenderSetup.builder(CmRenderPipelines.LINES)
                    .sortOnUpload()
                    .createRenderSetup()
    );

    public static final RenderType LINES_THROUGH_WALLS = RenderType.create(
            "lines_through_walls",
            RenderSetup.builder(CmRenderPipelines.LINES_THROUGH_WALLS)
                    .sortOnUpload()
                    .createRenderSetup()
    );

    public static RenderType getLines(boolean throughWalls) {
        return throughWalls ? LINES_THROUGH_WALLS : LINES;
    }
}

class CmRenderPipelines {

    public static final RenderPipeline FILLED_BOX_THROUGH_WALLS = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_filled_box_through_walls"))
                    .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
                    .withPrimitiveTopology(PrimitiveTopology.TRIANGLE_STRIP)
                    .build()
    );

    public static final RenderPipeline LINES = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/line_strip"))
                    .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_NORMAL)
                    .withPrimitiveTopology(PrimitiveTopology.LINES)
                    .withCull(false)
                    .build()
    );

    public static final RenderPipeline LINES_THROUGH_WALLS = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/line_through_walls"))
                    .withShaderDefine("shad")
                    .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_NORMAL)
                    .withPrimitiveTopology(PrimitiveTopology.LINES)
                    .withCull(false)
                    .build()
    );
}