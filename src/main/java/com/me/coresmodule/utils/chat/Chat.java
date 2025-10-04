package com.me.coresmodule.utils.chat;

import static com.me.coresmodule.CoresModule.mc;
import net.minecraft.text.Text;

public class Chat {

    /**
     * Shows a local chat message only visible to the player.
     * @param s The message to display in the chat.
     */
    public static void chat(String s) {
        mc.inGameHud.getChatHud().addMessage(Text.of(s.replaceAll("&", "§")));
    }

    /**
     * Sends a command to the server.
     * This correctly simulates a player typing a command.
     * @param command The command to send, without the leading slash.
     */
    public static void command(String command) {
        if (mc.player != null && mc.player.networkHandler != null) {
            if (!command.startsWith("/")) mc.player.networkHandler.sendChatMessage("/" + command);
            else mc.player.networkHandler.sendChatMessage(command);
        }
    }
}
