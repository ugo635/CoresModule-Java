package com.me.coresmodule.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeaconRenderer.class)
public interface BeaconBlockEntityRendererInvoker {
    @Invoker("Lnet/minecraft/client/renderer/blockentity/BeaconRenderer;submitBeaconBeam(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;FFIII)V")
    static void renderBeam(PoseStack matrices, SubmitNodeCollector queue, float scale, float rotationDegrees, int minHeight, int maxHeight, int color) {
        throw new UnsupportedOperationException();
    }
}