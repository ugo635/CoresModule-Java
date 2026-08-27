package com.me.coresmodule.utils;

import com.me.coresmodule.utils.chat.Chat;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Screenshot;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static com.me.coresmodule.CoresModule.mc;


public class ScreenshotUtils {

    /*
     * The actual GPU readback (Screenshot.takeScreenshot) has to happen on the
     * render thread, but everything after that - converting pixels to a
     * BufferedImage, writing the PNG to disk, and especially spawning a whole
     * second JVM to copy the image to the clipboard - does NOT need the GL
     * context. Doing that work synchronously inside the render-thread callback
     * is what was freezing the screen (forking a JVM alone can easily take
     * several hundred ms). We now hand all of that off to this single
     * background thread instead.
     */
    private static final ExecutorService SCREENSHOT_EXECUTOR = Executors.newSingleThreadExecutor(
            runnable -> {
                Thread thread = new Thread(runnable, "CoresModule-Screenshot");
                thread.setDaemon(true);
                return thread;
            }
    );

    public static void takeScreenshot() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        RenderTarget framebuffer = client.getMainRenderTarget();

        Screenshot.takeScreenshot(framebuffer, nativeImage -> {
            // We're on the render thread here - do the bare minimum (just hand
            // off the already-captured NativeImage) and get out immediately.
            SCREENSHOT_EXECUTOR.execute(() -> {
                try {
                    BufferedImage bufferedImage = new BufferedImage(
                            nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB
                    );
                    for (int y = 0; y < nativeImage.getHeight(); y++) {
                        for (int x = 0; x < nativeImage.getWidth(); x++) {
                            bufferedImage.setRGB(x, y, nativeImage.getPixel(x, y));
                        }
                    }

                    boolean copied = copyImageToClipboard(bufferedImage);
                    String savedPath = saveToFile(bufferedImage);

                    if (copied) {
                        mc.execute(() -> {
                            try {
                                Chat.clickableChat(
                                        "§6[CM] §aScreenshot copied to clipboard and saved here.",
                                        "§e" + savedPath,
                                        savedPath,
                                        "OpenFile"
                                );
                            } catch (URISyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        mc.execute(() -> {
                            try {
                                Chat.clickableChat(
                                        "§6[CM] §aScreenshot saved here.",
                                        "§e" + savedPath,
                                        savedPath,
                                        "OpenFile"
                                );
                            } catch (URISyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mc.execute(() -> Chat.chat("§6[CM] Failed to take screenshot."));
                } finally {
                    nativeImage.close();
                }
            });
        });
    }




    public static CompletableFuture<BufferedImage> takeScreenshotWithReturn() {
        CompletableFuture<BufferedImage> future = new CompletableFuture<>();

        if (mc.player == null) {
            future.complete(null);
            return future;
        }

        RenderTarget framebuffer = mc.getMainRenderTarget();

        Minecraft.getInstance().execute(() -> {
            Screenshot.takeScreenshot(framebuffer, nativeImage -> {
                // Same idea as takeScreenshot(): only the readback needed the
                // render thread. Build the BufferedImage off-thread so callers
                // (e.g. the Discord bot's /takescreenshot) don't stall the game.
                SCREENSHOT_EXECUTOR.execute(() -> {
                    try {
                        BufferedImage image = new BufferedImage(
                                nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB
                        );

                        for (int y = 0; y < nativeImage.getHeight(); y++) {
                            for (int x = 0; x < nativeImage.getWidth(); x++) {
                                image.setRGB(x, y, nativeImage.getPixel(x, y));
                            }
                        }
                        // Successfully complete the future with the image
                        future.complete(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                        future.completeExceptionally(e);
                    } finally {
                        nativeImage.close();
                    }
                });
            });
        });

        return future;
    }

    private static String saveToFile(BufferedImage image) throws Exception {
        Minecraft client = Minecraft.getInstance();
        File screenshotDir = new File("screenshots");
        if (!screenshotDir.exists()) screenshotDir.mkdirs();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        File outputFile = new File(screenshotDir, "screenshot_" + timestamp + ".png");
        javax.imageio.ImageIO.write(image, "png", outputFile);

        return screenshotDir.getAbsolutePath();
    }

    private static String saveToFileWithName(BufferedImage image) throws Exception {
        Minecraft client = Minecraft.getInstance();
        File screenshotDir = new File("screenshots");
        if (!screenshotDir.exists()) screenshotDir.mkdirs();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        File outputFile = new File(screenshotDir, "screenshot_" + timestamp + ".png");
        javax.imageio.ImageIO.write(image, "png", outputFile);

        return outputFile.getAbsolutePath();
    }

    /**
     * Copies a BufferedImage to the system clipboard using forked process
     * @param image The image to copy
     * @return true if successful, false otherwise
     */
    public static boolean copyImageToClipboard(BufferedImage image) {
        File tempFile = null;
        try {
            // Save to temporary file
            tempFile = File.createTempFile("screenshot_", ".png");
            javax.imageio.ImageIO.write(image, "png", tempFile);

            // Use forked process to copy to clipboard
            try (ForkedImageClipboard clipboard = new ForkedImageClipboard()) {
                return clipboard.copy(tempFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // Clean up temp file
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    private static class ImageSelection implements Transferable {
        private final Image image;

        public ImageSelection(Image image) {
            this.image = image;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] {DataFlavor.imageFlavor};
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