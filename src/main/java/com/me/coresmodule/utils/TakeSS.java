package com.me.coresmodule.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class TakeSS {

    public static void copyScreenshotToClipboard() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getFramebuffer() == null) return;

        File screenshotsDir = new File(client.runDirectory, "screenshots");
        screenshotsDir.mkdirs();

        // Create a unique file inside the screenshots folder
        String filename = "clipboard_" + new Date().getTime() + ".png";
        File screenshotFile = new File(screenshotsDir, filename);

        Framebuffer framebuffer = client.getFramebuffer();

        // Save the screenshot normally
        ScreenshotRecorder.saveScreenshot(screenshotFile, framebuffer, (Text result) -> {
            try {
                // Wait a small moment to ensure file is written
                Thread.sleep(100);

                if (!screenshotFile.exists()) {
                    System.err.println("Screenshot file was not created: " + screenshotFile.getAbsolutePath());
                    return;
                }

                BufferedImage image = ImageIO.read(screenshotFile);
                if (image != null) {
                    copyImageToClipboard(image);
                    screenshotFile.delete();

                    client.execute(() -> {
                        if (client.player != null)
                            client.player.sendMessage(Text.of("§a[CM] Screenshot copied to clipboard!"), false);
                    });
                } else {
                    System.err.println("Failed to load screenshot image from: " + screenshotFile.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void copyImageToClipboard(BufferedImage image) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("Clipboard unavailable (headless environment).");
            return;
        }

        TransferableImage trans = new TransferableImage(image);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(trans, null);
    }

    private static class TransferableImage implements Transferable {
        private final Image image;

        public TransferableImage(Image image) {
            this.image = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }
}
