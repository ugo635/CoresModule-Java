package com.me.coresmodule.utils;

import com.me.coresmodule.utils.chat.Chat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ScreenshotRecorder;

import static com.me.coresmodule.utils.Helper.sleep;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ScreenshotUtils {

    public static void takeScreenshot(int delay) {
        Helper.sleep(delay, () -> {
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

                    if (GraphicsEnvironment.isHeadless()) {
                        String savedPath = saveToFile(bufferedImage);
                        Chat.clickableChat(
                                "§6[CM] §cHeadless environment detected! Screenshot saved here.",
                                "§e"+savedPath,
                                savedPath,
                                "OpenFile" // Opens the folder in system file explorer
                        );
                    } else {
                        copyToClipboard(bufferedImage);
                        Chat.chat("§6[CM] §aScreenshot copied to clipboard!");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        String savedPath = saveToFileFromError(e);
                        Chat.clickableChat(
                                "§6[CM] §cClipboard copy failed! Screenshot saved here.",
                                "§e"+savedPath,
                                savedPath,
                                "OpenFile"
                        );
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Chat.chat("§6[CM]] Failed to take screenshot.");
                    }
                } finally {
                    nativeImage.close();
                }
            });
        });
    }

    public static void takeScreenshot() {takeScreenshot(0);}

    private static void copyToClipboard(BufferedImage image) throws Exception {
        ImageSelection imgSel = new ImageSelection(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }

    private static String saveToFile(BufferedImage image) throws Exception {
        MinecraftClient client = MinecraftClient.getInstance();
        File screenshotDir = new File(client.runDirectory, "screenshots");
        if (!screenshotDir.exists()) screenshotDir.mkdirs();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        File outputFile = new File(screenshotDir, "screenshot_" + timestamp + ".png");
        javax.imageio.ImageIO.write(image, "png", outputFile);
        return outputFile.getAbsolutePath();
    }

    private static String saveToFileFromError(Exception e) throws Exception {
        MinecraftClient client = MinecraftClient.getInstance();

        File screenshotDir = new File(client.runDirectory, "screenshots");
        if (!screenshotDir.exists()) screenshotDir.mkdirs();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        String fileName = "screenshot_error_" + timestamp + ".png";
        File outputFile = new File(screenshotDir, fileName);

        BufferedImage img = new BufferedImage(200, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.drawString("Screenshot failed: " + e.getMessage(), 10, 25);
        g.dispose();

        javax.imageio.ImageIO.write(img, "png", outputFile);

        return screenshotDir.getAbsolutePath();
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
