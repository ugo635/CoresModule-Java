package com.me.coresmodule.utils.chat;

import static com.me.coresmodule.CoresModule.mc;
import static java.lang.Integer.parseInt;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class Chat {

    /**
     * Shows a local chat message only visible to the player.
     * @param s The message to display in the chat.
     */
    public static void chat(String s) {
        mc.gui.getChat().addClientSystemMessage(Component.literal(s.replaceAll("&", "§")));
    }

    /**
     * Shows a local chat message only visible to the player.
     * @param t The Component to display in the chat.
     */
    public static void chat(Component t) {
        mc.gui.getChat().addClientSystemMessage(t);
    }

    /**
     * Sends a command to the server.
     * This correctly simulates a player typing a command.
     * @param command The command to send, without the leading slash.
     */
    public static void command(String command) {
        if (mc.player == null) return;
        if (!command.startsWith("/")) {
            mc.player.connection.sendChat("/" + command);
        } else {
            mc.player.connection.sendChat(command);
        }
    }

    /**
     * Sends a clickable message to the player with a hover tooltip and custom callback.
     * Requires ClickActionManager to be implemented.
     *
     * @param message The text to display.
     * @param hover   The text to show on hover.
     * @param onClick The code to run when clicked.
     */
    public static void clickableChat(String message, String hover, Runnable onClick) {
        UUID actionId = ClickActionManager.registerAction(onClick);

        Component hoverComponent = Component.literal(hover).withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
        ClickEvent clickEvent = new ClickEvent.RunCommand("__cm_run_clickable_action");
        HoverEvent hoverEvent = new HoverEvent.ShowText(hoverComponent);

        Component styledComponent = Component.literal(message).setStyle(
                Style.EMPTY
                        .withClickEvent(clickEvent)
                        .withHoverEvent(hoverEvent)
        );

        if (mc.player != null) {
            chat(styledComponent);
        }
    }

    /**
     * Sends a simple clickable chat message that runs a command.
     *
     * @param message The text to display.
     * @param hover   The hover tooltip text.
     * @param command The command to execute when clicked.
     * @param typeC The type of ClickEvent by default ClickEvent.RunCommand
     */
    public static void clickableChat(String message, String hover, String command, String typeC) throws URISyntaxException {
        ClickEvent clickEvent;
        HoverEvent hoverEvent;

        clickEvent = switch (typeC) {
            case "OpenUrl" -> new ClickEvent.OpenUrl(new URI(command));
            case "ChangePage" -> new ClickEvent.ChangePage(parseInt(command));
            case "CopyToClipboard" -> new ClickEvent.CopyToClipboard(command);
            case "SuggestCommand" -> new ClickEvent.SuggestCommand(command);
            case "OpenFile" -> new ClickEvent.OpenFile(command);
            default -> new ClickEvent.RunCommand(command);
        };

        hoverEvent = new HoverEvent.ShowText(Component.literal(hover));

        Component styledComponent = Component.literal(message).setStyle(
                Style.EMPTY
                        .withClickEvent(clickEvent)
                        .withHoverEvent(hoverEvent)
        );

        if (mc.player != null) {
            chat(styledComponent);
        }
    }

    /**
     * Sends a simple clickable chat message that runs a command.
     *
     * @param message The text to display.
     * @param hover   The hover tooltip text.
     * @param command The command to execute when clicked.
     */
    public static void clickableChat(String message, String hover, String command) {
        Component hoverComponent = Component.literal(hover);
        ClickEvent clickEvent = new ClickEvent.RunCommand(command);
        HoverEvent hoverEvent = new HoverEvent.ShowText(hoverComponent);

        Component styledComponent = Component.literal(message).setStyle(
                Style.EMPTY
                        .withClickEvent(clickEvent)
                        .withHoverEvent(hoverEvent)
        );

        if (mc.player != null) {
            chat(styledComponent);
        }
    }

    /**
     * Gets a message that fills one line of chat by repeating the separator.
     *
     * @param separator  The string to repeat. Defaults to "-".
     * @param colorCodes The color codes to apply (e.g., "§b").
     * @return The message string that fills the chat line.
     */
    public static String getChatBreak(String separator, String colorCodes) {
        if (separator == null || separator.isEmpty()) {
            return "";
        }

        Font font = mc.font;
        int chatWidth = mc.gui.getChat().getWidth();
        int separatorWidth = font.width(separator);

        if (separatorWidth <= 0) {
            return "";
        }

        int repeatCount = chatWidth / separatorWidth;
        return colorCodes + separator.repeat(repeatCount);
    }

    /**
     * Overload with default parameters.
     */
    public static String getChatBreak() {
        return getChatBreak("-", "§b");
    }

    /**
     * Sends a message to the server chat.
     * @param message The message to send.
     */
    public static void say(String message) {
        if (mc.player != null) {
            mc.player.connection.sendChat(message);
        }
    }
}
