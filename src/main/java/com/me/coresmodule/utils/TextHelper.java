package com.me.coresmodule.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

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


    public static ClickEvent getFullClickEvent(Text t) {
        if (getClickEvent(t) == null) return null;
        else return stringToClickEvent(getClickEvent(t).get("action"), getClickEvent(t).get("value"));
    }

    public static HoverEvent getFullHoverEvent(Text t) {
        if (getHoverEvent(t) == null) return null;
        else return stringToHoverEvent(getHoverEvent(t).get("value"));
    }

    public static Map<String, String> getClickEvent(Text message) {
        ClickEvent click = message.getStyle().getClickEvent();
        if (click == null) return null;

        Map<String, String> result = new HashMap<>();

        if (click instanceof ClickEvent.RunCommand runCommand) {
            result.put("action", "run_command");
            result.put("value", runCommand.command());
        } else if (click instanceof ClickEvent.OpenUrl openUrl) {
            result.put("action", "open_url");
            result.put("value", String.valueOf(openUrl.uri()));
        } else if (click instanceof ClickEvent.SuggestCommand suggestCommand) {
            result.put("action", "suggest_command");
            result.put("value", suggestCommand.command());
        } else if (click instanceof ClickEvent.ChangePage changePage) {
            result.put("action", "change_page");
            result.put("value", String.valueOf(changePage.page()));
        } else if (click instanceof  ClickEvent.OpenFile openFile) {
            result.put("action", "open_file");
            result.put("value", openFile.path());
        } else if (click instanceof ClickEvent.CopyToClipboard copyToClipboard) {
            result.put("action", "copy_to_clipboard");
            result.put("value", copyToClipboard.value());
        } else {
            result.put("action", "unknown");
            result.put("value", click.getClass().getSimpleName());
        }

        return result;
    }

    public static Map<String, String> getHoverEvent(Text message) {
        HoverEvent hover = message.getStyle().getHoverEvent();
        if (hover == null) return null;

        Map<String, String> result = new HashMap<>();

        if (hover instanceof HoverEvent.ShowText showText) {
            Text text = showText.value();
            result.put("action", "show_text");
            result.put("value", getFormattedString(text));
        } else if (hover instanceof HoverEvent.ShowItem showItem) {
            ItemStack itemStack = showItem.item();
            result.put("action", "show_item");
            result.put("value", itemStack.getName().getString());
        } else if (hover instanceof HoverEvent.ShowEntity showEntity) {
            HoverEvent.EntityContent entity = showEntity.entity();
            String name = String.valueOf(entity.name);
            String entityName = name != null ? name : "Unnamed entity";
            result.put("action", "show_entity");
            result.put("value", entityName);
        } else {
            result.put("action", "unknown");
            result.put("value", hover.getClass().getSimpleName());
        }

        return result;
    }

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

    public static ClickEvent stringToClickEvent(String command) {
        return stringToClickEvent("", command);
    }

    public static HoverEvent stringToHoverEvent(String hover) {
        return new HoverEvent.ShowText(Text.of(hover));
    }

    public static boolean noActions(Text t) {
        return (getClickEvent(t) == null && getHoverEvent(t) == null);
    }

    public static boolean noActionsAndDontStartWithStyle(Text t) {
        return (getClickEvent(t) == null && getHoverEvent(t) == null && getFormattedString(t).startsWith("§"));
    }

    public static String removeFormatting(String input) {
        return input.replaceAll("§.", "");
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

    public static String formattedString(Text text) {
        return getFormattedString(text);
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
            default -> 'f';
        };
    }
}