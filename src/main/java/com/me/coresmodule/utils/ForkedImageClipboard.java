package com.me.coresmodule.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ForkedImageClipboard implements AutoCloseable {
    private final Process process;
    private final DataInputStream inputStream;

    public ForkedImageClipboard() throws IOException {
        // Start a new Java process running the clipboard main method
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = ForkedImageClipboard.class.getName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className
        );

        this.process = builder.start();
        this.inputStream = new DataInputStream(process.getInputStream());
    }

    public boolean copy(File file) throws IOException {
        OutputStream out = process.getOutputStream();
        out.write(file.getAbsoluteFile().toString().getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
        return inputStream.read() == 1;
    }

    @Override
    public void close() {
        try {
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This main method runs in the forked process
    public static void main(String[] args) {
        try {
            // Read the file path from stdin
            byte[] pathBytes = System.in.readAllBytes();
            String filePath = new String(pathBytes, StandardCharsets.UTF_8);
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.write(0); // Failure
                System.out.close();
                return;
            }

            // Copy to clipboard
            boolean success = copyToClipboard(file);

            System.out.write(success ? 1 : 0);
            System.out.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.write(0); // Failure
            System.out.close();
        }
    }

    private static boolean copyToClipboard(File file) {
        try {
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(file);
            if (image == null) return false;

            // Convert to RGB
            java.awt.image.BufferedImage rgbImage = new java.awt.image.BufferedImage(
                    image.getWidth(), image.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB
            );
            java.awt.Graphics g = rgbImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();

            // Copy to clipboard
            java.awt.datatransfer.Clipboard clipboard =
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new ImageTransferable(rgbImage), null);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class ImageTransferable implements java.awt.datatransfer.Transferable {
        private final java.awt.Image image;

        public ImageTransferable(java.awt.Image image) {
            this.image = image;
        }

        public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
            return new java.awt.datatransfer.DataFlavor[]{java.awt.datatransfer.DataFlavor.imageFlavor};
        }

        public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor flavor) {
            return java.awt.datatransfer.DataFlavor.imageFlavor.equals(flavor);
        }

        public Object getTransferData(java.awt.datatransfer.DataFlavor flavor)
                throws java.awt.datatransfer.UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor))
                throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
            return image;
        }
    }
}