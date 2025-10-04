package com.me.coresmodule.utils;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import java.util.function.Consumer;

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

}
