package com.me.coresmodule.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityRendererInvoker {
    @Invoker("renderBeam")
    static void renderBeam(PoseStack matrices, SubmitNodeCollector queue, float scale, float rotationDegrees, int minHeight, int maxHeight, int color) {
        throw new UnsupportedOperationException();
    }
}