package com.me.coresmodule.utils.render.gui.guis.ItemCustomizer;

import com.me.coresmodule.utils.ItemHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import gg.essential.elementa.UIComponent;
import gg.essential.universal.UGraphics;
import gg.essential.universal.UMatrixStack;
import gg.essential.universal.utils.ReleasedDynamicTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Field;

import static com.me.coresmodule.CoresModule.mc;
import static com.me.coresmodule.CoresModule.overrides;
import static gg.essential.elementa.utils.ImageKt.drawTexture;

public class AnimatedItemComponent extends UIComponent {
    private static float speed = 0.03f; // High number = faster, small number = slower

    private BufferedImage baseItemImage;
    private BufferedImage localGlintImage;
    private ReleasedDynamicTexture currentTexture;

    // Add these two lines to fix the errors
    private long lastFrameTime = 0;
    private static final long FRAME_TIME_MS = 50; // 20 FPS for the animation

    public AnimatedItemComponent() {
        loadItemTextures();
        loadLocalGlint();
        updateTexture();
    }

    public void reload() {
        loadItemTextures();
        loadLocalGlint();
        updateTexture();
    }

    private void loadLocalGlint() {
        try {
            InputStream is = this.getClass().getResourceAsStream("/assets/coresmodule/texture/enchant_glint.png");
            if (is != null) {
                localGlintImage = ImageIO.read(is);
                System.out.println("Glint loaded successfully!");
            } else {
                System.out.println("Glint file NOT found at path!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadItemTextures() {
        try {
            ItemStack heldItem = ItemHelper.getHeldItem();
            if (heldItem.isEmpty() || heldItem.getItem() == Items.AIR) {
                baseItemImage = createTestPattern();
                return;
            }

            Identifier itemId;
            String uuid = ItemHelper.getUUID(heldItem);
            if (overrides.containsKey(uuid)) {
                itemId = Registries.ITEM.getId(overrides.get(uuid).second.getItem());
            } else {
                itemId = Registries.ITEM.getId(heldItem.getItem());
            }

            Identifier spriteId = itemId.withPrefixedPath("item/");
            SpriteIdentifier spriteIdentifier = new SpriteIdentifier(
                    Identifier.of("minecraft", "textures/atlas/blocks.png"),
                    spriteId
            );
            Sprite sprite = mc.getAtlasManager().getSprite(spriteIdentifier);

            BufferedImage spriteImage = getBufferedImage(sprite);

            baseItemImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = baseItemImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(spriteImage, 0, 0, 128, 128, null);
            g2d.dispose();
        } catch (Exception e) {
            baseItemImage = createTestPattern();
        }
    }

    private void updateTexture() {
        BufferedImage combined = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combined.createGraphics();

        // 1. Draw the shovel base
        g2d.drawImage(baseItemImage, 0, 0, null);

        // 2. Overlap the MOVING glint
        if (localGlintImage != null && ItemHelper.getHeldItem().hasGlint()) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));

            // Calculate offset based on time
            long time = System.currentTimeMillis() % 10000;
            int offset = (int) (time * speed);

            AffineTransform old = g2d.getTransform();
            g2d.rotate(Math.toRadians(-45), 128, 128);

            // Tile the glint to loop
            for (int x = -512; x < 512; x += 128) {
                for (int y = -512; y < 512; y += 128) {
                    g2d.drawImage(localGlintImage, x + (offset % 128), y + (offset % 128), 128, 128, null);
                }
            }
            g2d.setTransform(old);
        }

        g2d.dispose();

        if (currentTexture != null) {
            currentTexture.close();
        }
        currentTexture = UGraphics.getTexture(combined);
        if (currentTexture != null) {
            currentTexture.uploadTexture();
        }
    }

    @Override
    public void draw(UMatrixStack matrixStack) {
        beforeDrawCompat(matrixStack);

        // Update animation logic
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= FRAME_TIME_MS) {
            lastFrameTime = currentTime;
            updateTexture();
        }

        if (currentTexture == null) return;

        float x = getLeft();
        float y = getTop();
        float width = getWidth();
        float height = getHeight();

        if (width <= 0 || height <= 0) return;

        RenderSystem.setShaderTexture(0, currentTexture.getGlTextureView());

        double scale = mc.getWindow().getScaleFactor();
        int windowHeight = mc.getWindow().getHeight();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) (x * scale),
                (int) (windowHeight - (y + height) * scale),
                (int) (width * scale),
                (int) (height * scale)
        );

        drawTexture(
                matrixStack,
                currentTexture,
                Color.WHITE,
                (double) x,
                (double) y,
                (double) width,
                (double) height,
                GL11.GL_NEAREST,
                GL11.GL_NEAREST
        );

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        super.draw(matrixStack);
    }

    private static BufferedImage getBufferedImage(Sprite sprite) throws Exception {
        SpriteContents contents = sprite.getContents();
        Field imageField = SpriteContents.class.getDeclaredField("image");
        imageField.setAccessible(true);
        NativeImage nativeImage = (NativeImage) imageField.get(contents);

        int w = contents.getWidth();
        int h = contents.getHeight();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                img.setRGB(x, y, nativeImage.getColorArgb(x, y));
            }
        }
        return img;
    }

    private BufferedImage createTestPattern() {
        return new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
    }
}