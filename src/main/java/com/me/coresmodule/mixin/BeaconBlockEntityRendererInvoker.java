package com.me.coresmodule.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeaconBlockEntityRenderer.class)
public interface BeaconBlockEntityRendererInvoker {
    @Invoker("renderBeam")
    static void renderBeam(MatrixStack matrices, OrderedRenderCommandQueue queue, float scale, float rotationDegrees, int minHeight, int maxHeight, int color) {
        throw new UnsupportedOperationException();
    }
}