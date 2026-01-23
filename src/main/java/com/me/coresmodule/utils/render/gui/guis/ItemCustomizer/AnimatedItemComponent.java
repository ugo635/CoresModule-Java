package com.me.coresmodule.utils.render.gui.guis.ItemCustomizer;

import com.me.coresmodule.utils.ItemHelper;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIImage;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

import static com.me.coresmodule.CoresModule.mc;
import static com.me.coresmodule.CoresModule.overrides;

public class AnimatedItemComponent extends UIComponent {
    private static final Identifier GLINT_TEXTURE_ID = Identifier.of("minecraft", "textures/misc/enchanted_item_glint.png");

    private BufferedImage baseItemImage;
    private BufferedImage glintTexture;
    private boolean hasGlint;
    private long startTime;
    private UIComponent displayImage;
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL_MS = 16; // ~60 FPS

    public AnimatedItemComponent() {
        this.startTime = System.currentTimeMillis();
        loadItemTextures();

        // Create initial image (with glint if applicable)
        BufferedImage initialImage = hasGlint ? createAnimatedFrame() : baseItemImage;

        UIImage img = new UIImage(CompletableFuture.completedFuture(initialImage));
        displayImage = img.setX(new PixelConstraint(0))
                .setY(new PixelConstraint(0))
                .setWidth(new PixelConstraint(256))
                .setHeight(new PixelConstraint(256));
        displayImage.setChildOf(this);

        System.out.println("AnimatedItemComponent initialized. Has glint: " + hasGlint);
        System.out.println("Image size: " + baseItemImage.getWidth() + "x" + baseItemImage.getHeight());
        System.out.println("DisplayImage parent: " + (displayImage.getParent() != null ? "YES" : "NO"));
    }

    private void loadItemTextures() {
        try {
            ItemStack heldItem = ItemHelper.getHeldItem();

            System.out.println("Loading item texture. Held item: " + (heldItem.isEmpty() ? "EMPTY" : heldItem.getItem().toString()));

            if (heldItem.isEmpty() || heldItem.getItem() == Items.AIR) {
                // Create a test pattern instead of empty image
                baseItemImage = createTestPattern();
                hasGlint = false;
                System.out.println("No held item, creating test pattern");
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

            System.out.println("Item ID: " + itemId);

            Identifier spriteId = itemId.withPrefixedPath("item/");
            System.out.println("Sprite ID: " + spriteId);

            Sprite sprite = mc.getSpriteAtlas(Identifier.of("minecraft", "textures/atlas/blocks.png"))
                    .apply(spriteId);

            System.out.println("Sprite obtained: " + (sprite != null));

            BufferedImage spriteImage = getBufferedImage(sprite);
            System.out.println("Sprite image size: " + spriteImage.getWidth() + "x" + spriteImage.getHeight());

            // Check if the sprite is actually empty
            boolean hasPixels = false;
            for (int y = 0; y < spriteImage.getHeight() && !hasPixels; y++) {
                for (int x = 0; x < spriteImage.getWidth() && !hasPixels; x++) {
                    int alpha = (spriteImage.getRGB(x, y) >> 24) & 0xFF;
                    if (alpha > 0) {
                        hasPixels = true;
                    }
                }
            }
            System.out.println("Sprite has visible pixels: " + hasPixels);

            // Scale to 256x256
            baseItemImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = baseItemImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(spriteImage, 0, 0, 256, 256, null);
            g2d.dispose();

            System.out.println("Base item image created and scaled");

            // Check if item has glint
            hasGlint = heldItem.hasGlint();
            if (hasGlint) {
                System.out.println("Item has glint, loading glint texture...");
                loadGlintTexture();
            }

        } catch (Exception e) {
            System.err.println("Exception in loadItemTextures:");
            e.printStackTrace();
            baseItemImage = createTestPattern();
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
                    System.out.println("Trying glint path: " + path);
                    glintSprite = mc.getSpriteAtlas(Identifier.of("minecraft", "textures/atlas/blocks.png"))
                            .apply(path);
                    if (glintSprite != null) {
                        System.out.println("Found glint sprite at: " + path);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Failed to load glint from: " + path + " - " + e.getMessage());
                }
            }

            if (glintSprite != null) {
                BufferedImage rawGlint = getBufferedImage(glintSprite);
                System.out.println("Raw glint size: " + rawGlint.getWidth() + "x" + rawGlint.getHeight());
                glintTexture = applyEnchantmentTint(rawGlint);
                System.out.println("Glint texture loaded and tinted");
            } else {
                System.err.println("Could not load enchantment glint texture from any path");
                hasGlint = false;
            }
        } catch (Exception e) {
            System.err.println("Exception in loadGlintTexture:");
            e.printStackTrace();
            hasGlint = false;
        }
    }

    @Override
    public void draw(UMatrixStack matrixStack) {
        beforeDrawCompat(matrixStack);

        // Update the animated frame if glint is active, but only at intervals
        if (hasGlint && glintTexture != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime >= UPDATE_INTERVAL_MS) {
                lastUpdateTime = currentTime;

                BufferedImage animatedFrame = createAnimatedFrame();

                // Remove old image and create new one with updated frame
                removeChild(displayImage);
                UIImage img = new UIImage(CompletableFuture.completedFuture(animatedFrame));
                displayImage = img.setX(new PixelConstraint(0))
                        .setY(new PixelConstraint(0))
                        .setWidth(new PixelConstraint(256))
                        .setHeight(new PixelConstraint(256));
                displayImage.setChildOf(this);
            }
        }

        super.draw(matrixStack);
    }

    private BufferedImage createAnimatedFrame() {
        // Create composite image
        BufferedImage result = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = result.createGraphics();

        // Draw base item
        g2d.drawImage(baseItemImage, 0, 0, null);

        if (hasGlint && glintTexture != null) {
            // Calculate animation based on time (like NEU does)
            long currentTime = System.currentTimeMillis();
            float elapsed = (currentTime - startTime) / 1000.0f;

            // Two animated offsets for the shimmer effect
            float offset1 = (elapsed * 8.0f) % 1.0f;  // Speed for first pass
            float offset2 = (elapsed * 6.5f) % 1.0f; // Speed for second pass (different)

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            // First pass - diagonal scroll
            drawAnimatedGlintPass(g2d, offset1, -50.0f);

            // Second pass - opposite diagonal scroll
            drawAnimatedGlintPass(g2d, -offset2, 10.0f);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        g2d.dispose();
        return result;
    }

    private void drawAnimatedGlintPass(Graphics2D g2d, float offset, float rotation) {
        if (glintTexture == null) return;

        int glintSize = glintTexture.getWidth();

        // Scale up the glint texture
        int scaledSize = glintSize * 8;

        // Save transform
        AffineTransform oldTransform = g2d.getTransform();

        // Apply rotation around center
        g2d.translate(128, 128);
        g2d.rotate(Math.toRadians(rotation));
        g2d.translate(-128, -128);

        // Tile the glint texture with animation offset
        int tileOffset = (int) (offset * scaledSize);

        for (int y = -scaledSize * 2; y < 256 + scaledSize * 2; y += scaledSize) {
            for (int x = -scaledSize * 2; x < 256 + scaledSize * 2; x += scaledSize) {
                g2d.drawImage(glintTexture,
                        x + tileOffset,
                        y + tileOffset,
                        scaledSize,
                        scaledSize,
                        null);
            }
        }

        // Restore transform
        g2d.setTransform(oldTransform);
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

    private BufferedImage createTestPattern() {
        BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Draw a red and white checkerboard pattern so we can see if it's rendering
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if ((x + y) % 2 == 0) {
                    g2d.setColor(Color.RED);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillRect(x * 16, y * 16, 16, 16);
            }
        }

        g2d.dispose();
        return img;
    }

    public void refresh() {
        loadItemTextures();
        startTime = System.currentTimeMillis();
        lastUpdateTime = 0;

        if (displayImage != null) {
            removeChild(displayImage);
            BufferedImage initialImage = hasGlint ? createAnimatedFrame() : baseItemImage;
            UIImage img = new UIImage(CompletableFuture.completedFuture(initialImage));
            displayImage = img.setX(new PixelConstraint(0))
                    .setY(new PixelConstraint(0))
                    .setWidth(new PixelConstraint(256))
                    .setHeight(new PixelConstraint(256));
            displayImage.setChildOf(this);
        }
    }
}