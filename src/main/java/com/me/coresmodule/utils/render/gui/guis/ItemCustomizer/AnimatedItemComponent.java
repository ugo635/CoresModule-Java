package com.me.coresmodule.utils.render.gui.guis.ItemCustomizer;

import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.render.CustomItem.CustomItemRender;
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
    private static final float speed = 0.03f;
    private static final long FRAME_TIME_MS = 50; // 20 FPS

    private BufferedImage baseItemImage;
    private BufferedImage localGlintImage;
    private ReleasedDynamicTexture currentTexture;
    private long lastFrameTime = 0;
    private boolean hasGlint = false;
    private boolean needsTextureUpdate = true;

    public AnimatedItemComponent() {
        loadItemTextures();
        loadLocalGlint();
    }

    public void reload() {
        if (currentTexture != null) {
            currentTexture.close();
            currentTexture = null;
        }
        loadItemTextures();
        loadLocalGlint();
        needsTextureUpdate = true;
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

    private boolean shouldGlint(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!overrides.containsKey(ItemHelper.getUUID(stack))) return stack.hasGlint();
        Boolean glint = overrides.get(ItemHelper.getUUID(stack)).third;
        return glint != null && glint;
    }

    private void loadItemTextures() {
        try {
            ItemStack heldItem = ItemHelper.getHeldItem();
            if (heldItem.isEmpty() || heldItem.getItem() == Items.AIR) {
                baseItemImage = createTestPattern();
                hasGlint = false;
                return;
            }

            hasGlint = shouldGlint(heldItem);

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
            e.printStackTrace();
            baseItemImage = createTestPattern();
            hasGlint = false;
        }
    }

    private BufferedImage createAnimatedFrame() {
        if (baseItemImage == null) {
            return createTestPattern();
        }

        BufferedImage combined = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combined.createGraphics();

        // Draw base item
        g2d.drawImage(baseItemImage, 0, 0, null);

        // Draw animated glint if item has enchantment
        if (localGlintImage != null && hasGlint) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));

            // Calculate offset based on time
            long time = System.currentTimeMillis() % 10000;
            int offset = (int) (time * speed);

            AffineTransform old = g2d.getTransform();
            g2d.rotate(Math.toRadians(-45), 64, 64);

            // Tile the glint
            for (int x = -256; x < 256; x += 128) {
                for (int y = -256; y < 256; y += 128) {
                    g2d.drawImage(localGlintImage, x + (offset % 128), y + (offset % 128), 128, 128, null);
                }
            }
            g2d.setTransform(old);
        }

        g2d.dispose();
        return combined;
    }

    private void updateTexture() {
        BufferedImage frame = createAnimatedFrame();

        if (currentTexture != null) {
            currentTexture.close();
        }

        currentTexture = UGraphics.getTexture(frame);
        currentTexture.uploadTexture();
        System.out.println("Texture uploaded successfully!");
    }

    @Override
    public void draw(UMatrixStack matrixStack) {
        beforeDrawCompat(matrixStack);

        // Update texture if needed or if animation frame changed
        long currentTime = System.currentTimeMillis();
        if (needsTextureUpdate || (hasGlint && currentTime - lastFrameTime >= FRAME_TIME_MS)) {
            lastFrameTime = currentTime;
            updateTexture();
            needsTextureUpdate = false;
        }

        if (currentTexture == null) {
            System.out.println("No texture to draw!");
            super.draw(matrixStack);
            return;
        }

        float x = getLeft();
        float y = getTop();
        float width = getWidth();
        float height = getHeight();

        if (width <= 0 || height <= 0) {
            super.draw(matrixStack);
            return;
        }

        // Draw the texture using Elementa's utility
        drawTexture(
                matrixStack,
                currentTexture,
                Color.WHITE,
                (double) x,
                (double) y,
                (double) width,
                (double) height,
                org.lwjgl.opengl.GL11.GL_NEAREST,
                org.lwjgl.opengl.GL11.GL_NEAREST
        );

        super.draw(matrixStack);
    }

    private static BufferedImage getBufferedImage(Sprite sprite) {
        SpriteContents contents = sprite.getContents();
        NativeImage nativeImage = Helper.<NativeImage>getField(SpriteContents.class, "image", contents);

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
        BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 128, 128);
        g.dispose();
        return img;
    }
}