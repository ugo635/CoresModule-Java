package com.me.coresmodule.utils;

import com.  mojang. blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ProjectionType;
import net.minecraft.client.  MinecraftClient;
import net. minecraft.client.gl. Framebuffer;
import net. minecraft.client.gl.SimpleFramebuffer;
import net. minecraft.client.render.*;
import net.minecraft.client. render.item.ItemRenderer;
import net. minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import org.lwjgl.  BufferUtils;
import org.lwjgl.opengl.  GL11;

import java.awt. image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.  OptionalInt;

public class ItemRenderingHelper {
    public BufferedImage renderHeldItemToImage() {
        MinecraftClient mc = MinecraftClient.  getInstance();
        ItemStack heldItem = mc.player.getMainHandStack();

        if (heldItem.isEmpty()) {
            return createEmptyImage();
        }

        int size = 256;

        // Create and initialize framebuffer
        Framebuffer framebuffer = new SimpleFramebuffer("item_render", size, size, true);
        framebuffer.initFbo(size, size);

        // Setup projection matrix
        Matrix4f projectionMatrix = new Matrix4f().setOrtho(
                0.0f, size,
                size, 0.0f,
                1000.0f, 3000.0f
        );
        RenderSystem.setProjectionMatrix(projectionMatrix, ProjectionType.ORTHOGRAPHIC);

        // Setup matrices
        MatrixStack matrices = new MatrixStack();
        matrices.push();
        matrices.translate(0, 0, -2000);

        // Setup lighting
        DiffuseLighting.enableGuiDepthLighting();

        // Center and scale the item
        float scale = 16.0f;
        matrices. push();
        matrices.translate(size / 2.0f, size / 2.0f, 0);
        matrices.scale(scale, -scale, scale);

        // Create render pass and render the item
        try (var renderPass = RenderSystem.getDevice().createCommandEncoder()
                .createRenderPass(framebuffer.getColorAttachment(),
                        framebuffer.useDepthAttachment ?  /*OptionalInt.of(framebuffer.getDepthAttachment().getId())*/OptionalInt.empty() : OptionalInt.empty())) {

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            // Get vertex consumers
            VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();

            // Render the item
            ItemRenderer itemRenderer = mc.getItemRenderer();
            itemRenderer.renderItem(
                    heldItem,
                    net.minecraft.item.ItemDisplayContext.GUI,
                    LightmapTextureManager.MAX_LIGHT_COORDINATE,
                    OverlayTexture.DEFAULT_UV,
                    matrices,
                    immediate,
                    mc.world,
                    0
            );

            immediate.draw();
        }

        matrices.pop();
        DiffuseLighting.disableGuiDepthLighting();
        matrices.pop();

        // Read pixels - this part may need adjustment based on GpuTexture API
        ByteBuffer buffer = BufferUtils.createByteBuffer(size * size * 4);
        // Note: You may need to bind the texture first or use a different method
        // This is a placeholder - the actual pixel reading API might be different
        GL11.glReadPixels(0, 0, size, size, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        // Cleanup
        framebuffer.delete();

        // Convert to BufferedImage
        BufferedImage image = new BufferedImage(size, size, BufferedImage.  TYPE_INT_ARGB);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int index = (x + y * size) * 4;
                int r = buffer. get(index) & 0xFF;
                int g = buffer.  get(index + 1) & 0xFF;
                int b = buffer.get(index + 2) & 0xFF;
                int a = buffer.get(index + 3) & 0xFF;

                image.setRGB(x, size - 1 - y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }

        return image;
    }

    private BufferedImage createEmptyImage() {
        return new BufferedImage(256, 256, BufferedImage. TYPE_INT_ARGB);
    }
}