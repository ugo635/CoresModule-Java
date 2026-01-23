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
            // Get the enchantment glint texture
            Identifier glintId = Identifier.of("minecraft", "textures/misc/enchanted_glint_item.png");
            Sprite glintSprite = mc.getSpriteAtlas(Identifier.of("minecraft", "textures/atlas/blocks.png"))
                    .apply(Identifier.of("minecraft", "misc/enchanted_glint_item"));

            BufferedImage glintImage = getBufferedImage(glintSprite);

            // Scale the glint to match the item size
            BufferedImage scaledGlint = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D glintG2d = scaledGlint.createGraphics();
            glintG2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            // Tile the glint texture to fill the area
            int glintWidth = glintImage.getWidth();
            int glintHeight = glintImage.getHeight();
            for (int y = 0; y < height; y += glintHeight) {
                for (int x = 0; x < width; x += glintWidth) {
                    glintG2d.drawImage(glintImage, x, y, null);
                }
            }
            glintG2d.dispose();

            // Apply the glint with a purple tint and transparency
            BufferedImage tintedGlint = applyPurpleTint(scaledGlint);

            // Overlay the glint with partial transparency
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.drawImage(tintedGlint, 0, 0, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        } catch (Exception e) {
            // If glint loading fails, just skip it
            e.printStackTrace();
        }
    }

    private BufferedImage applyPurpleTint(BufferedImage source) {
        BufferedImage tinted = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Purple color for enchantment glint (similar to Minecraft's)
        float purpleR = 0.5f;
        float purpleG = 0.25f;
        float purpleB = 0.8f;

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                // Apply purple tint
                red = (int) (red * purpleR);
                green = (int) (green * purpleG);
                blue = (int) (blue * purpleB);

                int tintedArgb = (alpha << 24) | (red << 16) | (green << 16) | blue;
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