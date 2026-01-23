package com.me.coresmodule.utils;

import com.me.coresmodule.utils.render.gui.guis.ItemCustomization;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import static com.me.coresmodule.CoresModule.mc;
import static com.me.coresmodule.CoresModule.overrides;

public class ItemRenderingHelper {
    public BufferedImage renderHeldItemToImage() {
        ItemStack heldItem = ItemHelper.getHeldItem();

        if (heldItem.isEmpty() || heldItem.getItem() == Items.AIR) {
            return createEmptyImage();
        }

        try {
            // Get the item's identifier from the registry
            Identifier itemId;
            String uuid = ItemHelper.getUUID(heldItem);
            if (overrides.containsKey(uuid)) itemId = Registries.ITEM.getId(overrides.get(uuid).second.getItem());
            else itemId = Registries.ITEM.getId(heldItem.getItem());

            // Create sprite identifier: namespace stays the same, path becomes "item/{path}"
            Identifier spriteId = itemId.withPrefixedPath("item/");

            // Get sprite from the block/item atlas
            Sprite sprite = mc.getSpriteAtlas(Identifier.of("minecraft", "textures/atlas/blocks.png"))
                    .apply(spriteId);

            BufferedImage spriteImage = getBufferedImage(sprite);

            // Scale to 256x256
            BufferedImage scaledImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledImage.createGraphics();

            // Use nearest-neighbor for crisp pixel art scaling
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(spriteImage, 0, 0, 256, 256, null);

            // Add enchantment glint if the item has one
            if (heldItem.hasGlint()) {
                addEnchantmentGlint(g2d, 256, 256);
            }

            g2d.dispose();

            return scaledImage;

        } catch (Exception e) {
            e.printStackTrace();
            return createEmptyImage();
        }
    }

    private void addEnchantmentGlint(Graphics2D g2d, int width, int height) {
        try {
            // Try different possible glint texture locations
            Sprite glintSprite = null;
            Identifier[] possiblePaths = {
                    Identifier.of("minecraft", "misc/enchanted_glint_item"),
                    Identifier.of("minecraft", "misc/enchanted_item_glint"),
                    Identifier.of("minecraft", "textures/misc/enchanted_glint_item")
            };

            for (Identifier path : possiblePaths) {
                try {
                    glintSprite = mc.getSpriteAtlas(Identifier.of("minecraft", "textures/atlas/blocks.png"))
                            .apply(path);
                    if (glintSprite != null) break;
                } catch (Exception ignored) {}
            }

            if (glintSprite == null) {
                System.out.println("Could not load enchantment glint texture");
                return;
            }

            BufferedImage glintImage = getBufferedImage(glintSprite);

            // Create a tiled glint pattern
            BufferedImage tiledGlint = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tileG2d = tiledGlint.createGraphics();
            tileG2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            int glintWidth = glintImage.getWidth() * 16; // Scale up the glint texture
            int glintHeight = glintImage.getHeight() * 16;

            for (int y = 0; y < height; y += glintHeight) {
                for (int x = 0; x < width; x += glintWidth) {
                    tileG2d.drawImage(glintImage, x, y, glintWidth, glintHeight, null);
                }
            }
            tileG2d.dispose();

            // Apply purple/magenta tint
            BufferedImage tintedGlint = applyEnchantmentTint(tiledGlint);

            // Overlay with transparency using blend mode
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            g2d.drawImage(tintedGlint, 0, 0, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage applyEnchantmentTint(BufferedImage source) {
        BufferedImage tinted = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Minecraft's enchantment glint color (purple/magenta)
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                // Skip fully transparent pixels
                if (alpha == 0) {
                    tinted.setRGB(x, y, 0);
                    continue;
                }

                // Apply purple tint - fixed bit shifting!
                int tintedR = Math.min(255, (int) (red * 0.8 + 128 * 0.5));
                int tintedG = Math.min(255, (int) (green * 0.4 + 64 * 0.5));
                int tintedB = Math.min(255, (int) (blue * 1.0 + 255 * 0.5));

                int tintedArgb = (alpha << 24) | (tintedR << 16) | (tintedG << 8) | tintedB;
                tinted.setRGB(x, y, tintedArgb);
            }
        }

        return tinted;
    }

    private static @NotNull BufferedImage getBufferedImage(Sprite sprite) throws NoSuchFieldException, IllegalAccessException {
        SpriteContents contents = sprite.getContents();

        // Access the private image field via reflection
        Field imageField = SpriteContents.class.getDeclaredField("image");
        imageField.setAccessible(true);
        NativeImage nativeImage = (NativeImage) imageField.get(contents);

        int width = contents.getWidth();
        int height = contents.getHeight();

        // Create BufferedImage from sprite pixels
        BufferedImage spriteImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Read pixels using getColorArgb (returns ARGB format)
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
}