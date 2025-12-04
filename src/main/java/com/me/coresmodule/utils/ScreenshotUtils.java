package com.me.coresmodule.utils;

import com.me.coresmodule.utils.chat.Chat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ScreenshotRecorder;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;


public class ScreenshotUtils {

    public static void takeScreenshot() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        Framebuffer framebuffer = client.getFramebuffer();

        ScreenshotRecorder.takeScreenshot(framebuffer, nativeImage -> {
            try {
                BufferedImage bufferedImage = new BufferedImage(
                        nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB
                );
                for (int y = 0; y < nativeImage.getHeight(); y++) {
                    for (int x = 0; x < nativeImage.getWidth(); x++) {
                        bufferedImage.setRGB(x, y, nativeImage.getColorArgb(x, y));
                    }
                }

                    String savedPath = saveToFile(bufferedImage);
                    Chat.clickableChat(
                            "§6[CM] §aScreenshot saved here.",
                            "§e"+savedPath,
                            savedPath,
                            "OpenFile"
                    );

            } catch (Exception e) {
                e.printStackTrace();
                Chat.chat("§6[CM] Failed to take screenshot.");
            } finally {
                nativeImage.close();
            }
        });
    }




    public static void takeScreenshotAsync(Consumer<String> callback) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            callback.accept("");
            return;
        }

        Framebuffer framebuffer = client.getFramebuffer();

        MinecraftClient.getInstance().execute(() -> {
            ScreenshotRecorder.takeScreenshot(framebuffer, nativeImage -> {
                try {
                    BufferedImage bufferedImage = new BufferedImage(
                            nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB
                    );
                    for (int y = 0; y < nativeImage.getHeight(); y++) {
                        for (int x = 0; x < nativeImage.getWidth(); x++) {
                            bufferedImage.setRGB(x, y, nativeImage.getColorArgb(x, y));
                        }
                    }

                    String savedPath = "";
                    savedPath = saveToFileWithName(bufferedImage);
                    callback.accept(savedPath);

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.accept("");
                } finally {
                    nativeImage.close();
                }
            });
        });
    }

    private static String saveToFile(BufferedImage image) throws Exception {
        MinecraftClient client = MinecraftClient.getInstance();
        File screenshotDir = new File(client.runDirectory, "screenshots");
        if (!screenshotDir.exists()) screenshotDir.mkdirs();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        File outputFile = new File(screenshotDir, "screenshot_" + timestamp + ".png");
        javax.imageio.ImageIO.write(image, "png", outputFile);

        return screenshotDir.getAbsolutePath();
    }

    private static String saveToFileWithName(BufferedImage image) throws Exception {
        MinecraftClient client = MinecraftClient.getInstance();
        File screenshotDir = new File(client.runDirectory, "screenshots");
        if (!screenshotDir.exists()) screenshotDir.mkdirs();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        File outputFile = new File(screenshotDir, "screenshot_" + timestamp + ".png");
        javax.imageio.ImageIO.write(image, "png", outputFile);

        return outputFile.getAbsolutePath();
    }

    private static class ImageSelection implements Transferable {
        private final Image image;

        public ImageSelection(Image image) {
            this.image = image;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
            return image;
        }
    }
}
