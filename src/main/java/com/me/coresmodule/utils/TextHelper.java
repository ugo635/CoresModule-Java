package com.me.coresmodule.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.text.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class TextHelper {


    public static Text stringToText(String s, ClickEvent click, HoverEvent hover) {
        return Text.literal(s).setStyle(
                Style.EMPTY
                        .withClickEvent(click)
                        .withHoverEvent(hover)
        );
    }

    public static Text siblingsToText(List<Text> parts) {
        MutableText result = Text.empty();
        for (Text t : parts) {
            result.append(t);
        }
        return result;
    }

    public static Text addToText(Text t, Text t2) {
        return Text.empty().append(t).append(t2);
    }

    public static Text addToText(ArrayList<Text> texts) {
        MutableText result = Text.empty();
        for (Text t : texts) {
            result = result.append(t);
        }
        return result;
    }


    /**
     * Get full ClickEvent from Text
     * @param t
     * @return ClickEvent
     */
    public static ClickEvent getFullClickEvent(Text t) {
        if (getClickEvent(t) == null) return null;
        else return stringToClickEvent(getClickEvent(t).get("action"), getClickEvent(t).get("value"));
    }

    /**
     * Get full HoverEvent from Text
     * @param t
     * @return HoverEvent
     */
    public static HoverEvent getFullHoverEvent(Text t) {
        if (getHoverEvent(t) == null) return null;
        else return stringToHoverEvent(getHoverEvent(t).get("value"));
    }

    /**
     * Get ClickEvent details from Text
     * @param message
     * @return Map<String, String>
     */
    public static Map<String, String> getClickEvent(Text message) {
        ClickEvent click = message.getStyle().getClickEvent();
        if (click == null) return null;

        Map<String, String> result = new HashMap<>();

        switch (click) {
            case ClickEvent.RunCommand runCommand -> {
                result.put("action", "run_command");
                result.put("value", runCommand.command());
            }
            case ClickEvent.OpenUrl openUrl -> {
                result.put("action", "open_url");
                result.put("value", String.valueOf(openUrl.uri()));
            }
            case ClickEvent.SuggestCommand suggestCommand -> {
                result.put("action", "suggest_command");
                result.put("value", suggestCommand.command());
            }
            case ClickEvent.ChangePage changePage -> {
                result.put("action", "change_page");
                result.put("value", String.valueOf(changePage.page()));
            }
            case ClickEvent.OpenFile openFile -> {
                result.put("action", "open_file");
                result.put("value", openFile.path());
            }
            case ClickEvent.CopyToClipboard copyToClipboard -> {
                result.put("action", "copy_to_clipboard");
                result.put("value", copyToClipboard.value());
            }
            default -> {
                result.put("action", "unknown");
                result.put("value", click.getClass().getSimpleName());
            }
        }

        return result;
    }

    /**
     * Get HoverEvent details from Text
     * @param message
     * @return Map<String, String>
     */
    public static Map<String, String> getHoverEvent(Text message) {
        HoverEvent hover = message.getStyle().getHoverEvent();
        if (hover == null) return null;

        Map<String, String> result = new HashMap<>();

        switch (hover) {
            case HoverEvent.ShowText showText -> {
                Text text = showText.value();
                result.put("action", "show_text");
                result.put("value", getFormattedString(text));
            }
            case HoverEvent.ShowItem showItem -> {
                ItemStack itemStack = showItem.item();
                result.put("action", "show_item");
                result.put("value", itemStack.getName().getString());
            }
            case HoverEvent.ShowEntity showEntity -> {
                HoverEvent.EntityContent entity = showEntity.entity();
                String name = String.valueOf(entity.name);
                String entityName = name != null ? name : "Unnamed entity";
                result.put("action", "show_entity");
                result.put("value", entityName);
            }
            default -> {
                result.put("action", "unknown");
                result.put("value", hover.getClass().getSimpleName());
            }
        }

        return result;
    }

    /**
     * Convert string to ClickEvent
     * @param type
     * @param command
     * @return ClickEvent
     */
    public static ClickEvent stringToClickEvent(String type, String command) {
        return switch (type) {
            case "run_command" -> new ClickEvent.RunCommand(command);
            case "open_url" -> {
                try {
                    yield new ClickEvent.OpenUrl(new URI(command));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            case "open_file" -> new ClickEvent.OpenFile(command);
            case "change_page" -> new ClickEvent.ChangePage(parseInt(command));
            case "suggest_command" -> new ClickEvent.SuggestCommand(command);
            case "copy_to_clipboard" -> new ClickEvent.CopyToClipboard(command);
            default -> new ClickEvent.RunCommand(command);
        };
    }

    /**
     * Convert string to ClickEvent with default type "run_command"
     * @param command
     * @return ClickEvent
     */
    public static ClickEvent stringToClickEvent(String command) {
        return stringToClickEvent("", command);
    }

    /**
     * Convert string to HoverEvent
     * @param hover
     * @return HoverEvent
     */
    public static HoverEvent stringToHoverEvent(String hover) {
        return new HoverEvent.ShowText(Text.of(hover));
    }

    /**
     * Check if Text has no ClickEvent and no HoverEvent
     * @param t
     * @return boolean
     */
    public static boolean noActions(Text t) {
        return (getClickEvent(t) == null && getHoverEvent(t) == null);
    }

    /**
     * Check if Text has no ClickEvent, no HoverEvent, and doesn't start with formatting code
     * @param t
     * @return boolean
     */
    public static boolean noActionsAndDontStartWithStyle(Text t) {
        return (getClickEvent(t) == null && getHoverEvent(t) == null && getFormattedString(t).startsWith("§"));
    }

    /**
     * Remove formatting codes from string
     * @param input
     * @return String
     */
    public static String removeFormatting(String input) {
        return input.replaceAll("§.", "");
    }

    /**
     * Get formatted string from Text
     * @param text
     * @return String
     */
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

    public static String getUnFormattedString(Text input) {
        return getFormattedString(input).replaceAll("§.", "");
    }

    public static String unFormattedString(Text input) {
        return getFormattedString(input).replaceAll("§.", "");
    }

    public static String getUnFormattedString(String input) {
        return input.replaceAll("§.", "");
    }

    public static String unFormattedString(String input) {
        return input.replaceAll("§.", "");
    }

    public static String formattedString(Text text) {
        return getFormattedString(text);
    }

    public static Text mapToText (List<Map.Entry<String, Integer>> list) {
        MutableText base = Text.empty();
        for (Map.Entry<String, Integer> entry : list) {
            base.append(
                    Text.literal(entry.getKey()).withColor(entry.getValue())
            );
        }

        return base;

    }

    private static char getColorCodeFromName(String name) {
        return switch (name) {
            case "#000000" -> '0';
            case "#0000AA" -> '1';
            case "#00AA00" -> '2';
            case "#00AAAA" -> '3';
            case "#AA0000" -> '4';
            case "#AA00AA" -> '5';
            case "#FFAA00" -> '6';
            case "#AAAAAA" -> '7';
            case "#555555" -> '8';
            case "#5555FF" -> '9';
            case "#55FF55" -> 'a';
            case "#55FFFF" -> 'b';
            case "#FF5555" -> 'c';
            case "#FF55FF" -> 'd';
            case "#FFFF55" -> 'e';
            // case if it's as name rather than hex:
            case "black" -> '0';
            case "dark_blue" -> '1';
            case "dark_green" -> '2';
            case "dark_aqua" -> '3';
            case "dark_red" -> '4';
            case "dark_purple" -> '5';
            case "gold" -> '6';
            case "gray" -> '7';
            case "dark_gray" -> '8';
            case "blue" -> '9';
            case "green" -> 'a';
            case "aqua" -> 'b';
            case "red" -> 'c';
            case "light_purple" -> 'd';
            case "yellow" -> 'e';
            default -> 'f';
        };
    }

}