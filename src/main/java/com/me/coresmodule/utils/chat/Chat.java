package com.me.coresmodule.utils.chat;

import static com.me.coresmodule.CoresModule.mc;
import static java.lang.Integer.parseInt;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class Chat {

    /**
     * Shows a local chat message only visible to the player.
     * @param s The message to display in the chat.
     */
    public static void chat(String s) {
        mc.inGameHud.getChatHud().addMessage(Text.of(s.replaceAll("&", "§")));
    }

    /**
     * Shows a local chat message only visible to the player.
     * @param t The Text to display in the chat.
     */
    public static void chat(Text t) {
        mc.inGameHud.getChatHud().addMessage(t);
    }

    /**
     * Sends a command to the server.
     * This correctly simulates a player typing a command.
     * @param command The command to send, without the leading slash.
     */
    public static void command(String command) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.player.networkHandler == null) return;
        if (!command.startsWith("/")) {
            mc.player.networkHandler.sendChatMessage("/" + command);
        } else {
            mc.player.networkHandler.sendChatMessage(command);
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

        Text hoverText = Text.literal(hover).formatted(Formatting.YELLOW);
        ClickEvent clickEvent = new ClickEvent.RunCommand("__cm_run_clickable_action");
        HoverEvent hoverEvent = new HoverEvent.ShowText(hoverText);

        Text styledText = Text.literal(message).setStyle(
                Style.EMPTY
                        .withClickEvent(clickEvent)
                        .withHoverEvent(hoverEvent)
        );

        if (mc.player != null) {
            mc.inGameHud.getChatHud().addMessage(styledText);
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
        Text hoverText = Text.literal(hover).formatted(Formatting.YELLOW);
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

        hoverEvent = new HoverEvent.ShowText(Text.literal(hover));

        Text styledText = Text.literal(message).setStyle(
                Style.EMPTY
                        .withClickEvent(clickEvent)
                        .withHoverEvent(hoverEvent)
        );

        if (mc.player != null) {
            mc.inGameHud.getChatHud().addMessage(styledText);
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
        Text hoverText = Text.literal(hover);
        ClickEvent clickEvent = new ClickEvent.RunCommand(command);
        HoverEvent hoverEvent = new HoverEvent.ShowText(hoverText);

        Text styledText = Text.literal(message).setStyle(
                Style.EMPTY
                        .withClickEvent(clickEvent)
                        .withHoverEvent(hoverEvent)
        );

        if (mc.player != null) {
            mc.inGameHud.getChatHud().addMessage(styledText);
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

        TextRenderer textRenderer = mc.textRenderer;
        int chatWidth = mc.inGameHud.getChatHud().getWidth();
        int separatorWidth = textRenderer.getWidth(separator);

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
        if (mc.player != null && mc.player.networkHandler != null) {
            mc.player.networkHandler.sendChatMessage(message);
        }
    }
}
