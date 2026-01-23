package com.me.coresmodule.utils.render.gui.guis.ItemCustomizer;

import com.me.coresmodule.utils.ItemHelper;
import gg.essential.elementa.UIComponent;
import gg.essential.universal.UGraphics;
import gg.essential.universal.UMatrixStack;
import gg.essential.universal.utils.ReleasedDynamicTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import static com.me.coresmodule.CoresModule.mc;
import static com.me.coresmodule.CoresModule.overrides;

public class AnimatedItemComponent extends UIComponent {
    private BufferedImage baseItemImage;
    private BufferedImage glintTexture;
    private boolean hasGlint;
    private long startTime;
    private ReleasedDynamicTexture currentTexture;

    public AnimatedItemComponent() {
        this.startTime = System.currentTimeMillis();
        loadItemTextures();
    }

    private void loadItemTextures() {
        try {
            ItemStack heldItem = ItemHelper.getHeldItem();

            if (heldItem.isEmpty() || heldItem.getItem() == Items.AIR) {
                baseItemImage = createEmptyImage();
                hasGlint = false;
                return;
            }

            // Load base item texture
            Identifier itemId;
            String uuid = ItemHelper.getUUID(heldItem);
            if (overrides.containsKey(uuid)) {
                itemId = Registries.ITEM.getId(overrides.get(uuid).second.getItem());
            } else {
                itemId = Registries.ITEM.getId(heldItem.getItem());
            }

            Identifier spriteId = itemId.withPrefixedPath("item/");
            Sprite sprite = mc.getSpriteAtlas(Identifier.of("minecraft", "textures/atlas/blocks.png"))
                    .apply(spriteId);

            BufferedImage spriteImage = getBufferedImage(sprite);

            // Scale to 256x256
            baseItemImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = baseItemImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(spriteImage, 0, 0, 256, 256, null);
            g2d.dispose();

            // Check if item has glint
            hasGlint = heldItem.hasGlint();
            if (hasGlint) {
                loadGlintTexture();
            }

        } catch (Exception e) {
            e.printStackTrace();
            baseItemImage = createEmptyImage();
            hasGlint = false;
        }
    }

    private void loadGlintTexture() {
        try {
            Sprite glintSprite = null;
            Identifier[] possiblePaths = {
                    Identifier.of("minecraft", "misc/enchanted_glint_item"),
                    Identifier.of("minecraft", "misc/enchanted_item_glint"),
            };

            for (Identifier path : possiblePaths) {
                try {
                    glintSprite = mc.getSpriteAtlas(Identifier.of("minecraft", "textures/atlas/blocks.png"))
                            .apply(path);
                    if (glintSprite != null) break;
                } catch (Exception ignored) {}
            }

            if (glintSprite != null) {
                BufferedImage glintImage = getBufferedImage(glintSprite);
                glintTexture = applyEnchantmentTint(glintImage);
            } else {
                System.out.println("Could not load enchantment glint texture");
                hasGlint = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            hasGlint = false;
        }
    }

    @Override
    public void draw(UMatrixStack matrixStack) {
        beforeDrawCompat(matrixStack);

        if (baseItemImage != null) {
            // Create animated frame
            BufferedImage frame = hasGlint && glintTexture != null
                    ? createAnimatedFrame()
                    : baseItemImage;

            // Clean up old texture
            if (currentTexture != null) {
                currentTexture.close();
            }

            // Create new texture from frame
            currentTexture = UGraphics.getTexture(frame);
            if (currentTexture != null) {
                currentTexture.uploadTexture();

                // Draw the texture using OpenGL
                drawTextureDirectly(currentTexture);
            }
        }

        super.draw(matrixStack);
    }

    private void drawTextureDirectly(ReleasedDynamicTexture texture) {
        float x = getLeft();
        float y = getTop();
        float width = getWidth();
        float height = getHeight();
        Color color = getColor();

        // Enable blending
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Enable textures
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getDynamicGlId());

        // Set texture parameters
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        // Set color
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;
        GL11.glColor4f(r, g, b, a);

        // Draw quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + width, y);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x, y + height);
        GL11.glEnd();

        // Reset state
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private BufferedImage createAnimatedFrame() {
        // Create composite image
        BufferedImage result = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = result.createGraphics();

        // Draw base item
        g2d.drawImage(baseItemImage, 0, 0, null);

        if (hasGlint && glintTexture != null) {
            // Calculate animation offset
            long currentTime = System.currentTimeMillis();
            float elapsed = (currentTime - startTime) / 1000.0f;
            float speed = 50f;
            int offsetX = (int) ((elapsed * speed) % (glintTexture.getWidth() * 16));
            int offsetY = (int) ((elapsed * speed * 0.5f) % (glintTexture.getHeight() * 16));

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            int glintWidth = glintTexture.getWidth() * 16;
            int glintHeight = glintTexture.getHeight() * 16;

            // Enable transparency
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));

            // Tile the glint with animation offset
            for (int y = -glintHeight; y < 256 + glintHeight; y += glintHeight) {
                for (int x = -glintWidth; x < 256 + glintWidth; x += glintWidth) {
                    g2d.drawImage(glintTexture,
                            x + offsetX,
                            y + offsetY,
                            glintWidth,
                            glintHeight,
                            null);
                }
            }
        }

        g2d.dispose();
        return result;
    }

    private BufferedImage applyEnchantmentTint(BufferedImage source) {
        BufferedImage tinted = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                if (alpha == 0) {
                    tinted.setRGB(x, y, 0);
                    continue;
                }

                // Apply purple/magenta tint
                int tintedR = Math.min(255, (int) (red * 0.8 + 128 * 0.5));
                int tintedG = Math.min(255, (int) (green * 0.4 + 64 * 0.5));
                int tintedB = Math.min(255, (int) (blue * 1.0 + 255 * 0.5));

                int tintedArgb = (alpha << 24) | (tintedR << 16) | (tintedG << 8) | tintedB;
                tinted.setRGB(x, y, tintedArgb);
            }
        }

        return tinted;
    }

    private static BufferedImage getBufferedImage(Sprite sprite) throws NoSuchFieldException, IllegalAccessException {
        SpriteContents contents = sprite.getContents();

        Field imageField = SpriteContents.class.getDeclaredField("image");
        imageField.setAccessible(true);
        NativeImage nativeImage = (NativeImage) imageField.get(contents);

        int width = contents.getWidth();
        int height = contents.getHeight();

        BufferedImage spriteImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = nativeImage.getColorArgb(x, y);
                spriteImage.setRGB(x, y, argb);
            }
        }
        return spriteImage;
    }

    private BufferedImage createEmptyImage() {
        return new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
    }

    public void refresh() {
        if (currentTexture != null) {
            currentTexture.close();
            currentTexture = null;
        }
        loadItemTextures();
        startTime = System.currentTimeMillis();
    }
}