package com.me.coresmodule.utils;

import com.me.coresmodule.utils.chat.Chat;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.me.coresmodule.CoresModule.mc;

public class Helper {


    public static void sleep(long milliseconds, Runnable callback) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
                callback.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static void print(String s) {
        if (!s.contains("❈") && !s.contains("❤")) System.out.println("[CoresModule] " + s);
    }

    public static void printErr(String s) {
        System.err.println("[CoresModule] " + s);
    }

    /**
     *
     * @param title Title
     * @param subtitle Subtitle
     * @param fadeIn How long for the text to fade in (In ticks)
     * @param time How long it stays (In ticks)
     * @param fadeOut How long for the text to fade out (In ticks)
     */
    public static void showTitle(String title, String subtitle, int fadeIn, int time, int fadeOut) {
        if (mc.inGameHud != null) {
            mc.inGameHud.setTitleTicks(fadeIn, time, fadeOut);

            if (title != null) {
                mc.inGameHud.setTitle(net.minecraft.text.Text.of(title));
            }

            if (subtitle != null) {
                mc.inGameHud.setSubtitle(net.minecraft.text.Text.of(subtitle));
            }
        }

    }

    public static String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "[" + LocalDateTime.now().format(formatter) + "]";
    }

    public static void exactSleep(long milliseconds, Runnable callback) {
        Thread thread = new Thread(() -> {
            long nanos = milliseconds * 1000000L;
            long start = System.nanoTime();

            try {
                if (milliseconds > 1) Thread.sleep(milliseconds - 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (System.nanoTime() - start < nanos) {
                Thread.onSpinWait();
            }

            callback.run();
        });

        thread.setDaemon(true);
        thread.start();
    }

    public static double roundToDecimals(double value, int decimals) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float roundToDecimals(float value, int decimals) {
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static String getGuiName() {
        return mc.currentScreen == null ? "" : mc.currentScreen.getTitle().getString();
    }

    public static void copyToClipboard(String s) {
        mc.keyboard.setClipboard(s);
    }

    public static void copyToClipboard(BufferedImage image) {
        ScreenshotUtils.copyImageToClipboard(image);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Class<?> clazz, String fieldName, Object instance) {
        try {
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return (T) f.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Class<?> clazz, String fieldName) {
        return (T) getField(clazz, fieldName, null);
    }
}
