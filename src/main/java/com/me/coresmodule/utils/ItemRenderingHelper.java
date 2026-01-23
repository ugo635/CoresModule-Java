package com.me.coresmodule.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

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

            // Scale to 256x256
            BufferedImage scaledImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledImage.createGraphics();

            // Use nearest-neighbor for crisp pixel art scaling
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(spriteImage, 0, 0, 256, 256, null);
            g2d.dispose();

            return scaledImage;

        } catch (Exception e) {
            e.printStackTrace();
            return createEmptyImage();
        }
    }

    private BufferedImage createEmptyImage() {
        return new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
    }
}