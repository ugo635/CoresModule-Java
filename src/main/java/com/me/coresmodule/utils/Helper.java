package com.me.coresmodule.utils;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import java.util.function.Consumer;

import static com.me.coresmodule.CoresModule.mc;

public class Helper {
    private static final Map<TextColor, Formatting> colorToFormatChar =
            Arrays.stream(Formatting.values())
                    .map(format -> {
                        TextColor color = TextColor.fromFormatting(format);
                        return color != null ? new AbstractMap.SimpleEntry<>(color, format) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private static @Nullable Character getColorFormatChar(TextColor color) {
        Formatting formatting = colorToFormatChar.get(color);
        return formatting != null ? formatting.getCode() : null;
    }

    public static String getFormatCodes(Style style) {
        StringBuilder builder = new StringBuilder();

        // Color
        if (style.getColor() != null) {
            Character colorChar = getColorFormatChar(style.getColor());
            if (colorChar != null) {
                builder.append('§').append(colorChar);
            }
        }

        // Formatting
        if (style.isBold()) builder.append("§l");
        if (style.isItalic()) builder.append("§o");
        if (style.isUnderlined()) builder.append("§n");
        if (style.isStrikethrough()) builder.append("§m");
        if (style.isObfuscated()) builder.append("§k");

        return builder.toString();
    }


    public static String formattedString(Text text) {
        StringBuilder builder = new StringBuilder();

        text.visit((style, content) -> {
            builder.append(getFormatCodes(style));
            builder.append(content);
            return Optional.empty();
        }, Style.EMPTY);

        return builder.toString();
    }

    public static String removeFormatting(String input) {
        return input.replaceAll("§.", "");
    }

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

    public static String getFormattedString(Text text) {
        StringBuilder sb = new StringBuilder();
        text.visit((style, string) -> {
            String color = style.getColor() != null ? style.getColor().getName() : null;
            if (color != null) {
                sb.append("§").append(getColorCodeFromName(color));
            }
            if (style.isBold()) sb.append("§l");
            if (style.isItalic()) sb.append("§o");
            if (style.isUnderlined()) sb.append("§n");
            if (style.isStrikethrough()) sb.append("§m");
            if (style.isObfuscated()) sb.append("§k");
            sb.append(string);
            return Optional.empty();
        }, Style.EMPTY);
        return sb.toString();
    }

    private static char getColorCodeFromName(String name) {
        switch (name.toLowerCase()) {
            case "black": return '0';
            case "dark_blue": return '1';
            case "dark_green": return '2';
            case "dark_aqua": return '3';
            case "dark_red": return '4';
            case "dark_purple": return '5';
            case "gold": return '6';
            case "gray": return '7';
            case "dark_gray": return '8';
            case "blue": return '9';
            case "green": return 'a';
            case "aqua": return 'b';
            case "red": return 'c';
            case "light_purple": return 'd';
            case "yellow": return 'e';
            case "white": return 'f';
            default: return 'f';
        }
    }


}
