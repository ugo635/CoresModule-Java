package com.me.coresmodule.features;

import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;

import static com.me.coresmodule.CoresModule.player;
import static com.me.coresmodule.utils.TextHelper.stringToText;

public class ColorReplacor {
    public static void register() {
        String[] testing_list = new String[]{
                "§r§8[§r§d331§r§8] §r§b§lᛝ §6[MVP§d++§6] %s§r: §fHi, I'm M++".formatted(player),
                "§r§8[§r§d331§r§8] §r§b§lᛝ §b[MVP§d+§b] %s§r: §fHi, I'm M+".formatted(player),
                "§r§8[§r§d331§r§8] §r§b§lᛝ §a[VIP§6+§a] %s§r: §fHi, I'm V+".formatted(player),
                "§r§8[§r§d331§r§8] §r§b§lᛝ §b[MVP§b] %s§r: §fHi, I'm M".formatted(player),
                "§r§8[§r§d331§r§8] §r§b§lᛝ §a[VIP§a] %s§r: §fHi, I'm V".formatted(player),
                "§r§8[§r§d331§r§8] §r§b§lᛝ §7%s§r§7: Hi, I'm rankless! Hi %s wsp?".formatted(player, player),
                "§6[MVP§d++§6] %s§r: §fHi, I'm M++".formatted(player),
                "§b[MVP§d+§b] %s§r: §fHi, I'm M+".formatted(player),
                "§a[VIP§6+§a] %s§r: §fHi, I'm V+".formatted(player),
                "§b[MVP§b] %s§r: §fHi, I'm M".formatted(player),
                "§a[VIP§a] %s§r: §fHi, I'm V".formatted(player),
                "§7%s§r§7: Hi, I'm rankless! Hi %s wsp?".formatted(player, player),
                "§r§9Party §8> §b[MVP§4+§b] Arcness§f§f: §rHi§r [MVP+] %s, wsp? ".formatted(player),
                "§r§9Party §8> §b[MVP§d+§b] %s: UwU§r".formatted(player),
                "§d§dTo §r§b[MVP§r§4+§r§b] Arcness§r§7: §7I am %s".formatted(player),
                "§9Party §8> §b[MVP§4+§b] N00DL3S_§f: Party > [MVP+] %s: Coords".formatted(player)
        };

        Register.command("colorTests", ignore -> {
            for (String msg : testing_list) {
                Chat.chat(msg);
            }
        }, "colorTest");


        Register.command("testMsg", ignore -> {
            Text msg;
            msg = stringToText("§bHello", TextHelper.stringToClickEvent("RUN_COMMAND", "/ah"), TextHelper.stringToHoverEvent("§eClick To Open The AH"));
            Chat.chat(msg);
            Chat.chat("You are " + player);
        });













        Register.onChatMessageCancelable(message -> {
            if (TextHelper.getFormattedString(message).contains("❈ Defense")) return false;
            String msgContent = TextHelper.getFormattedString(message);
            System.out.println("Message detected: " + msgContent);

            String colorForUsername = "§5";

            boolean isEdited = false;

            MutableText rebuilt = Text.empty();

            for (Text t : message.getSiblings()) {
                String part = TextHelper.getFormattedString(t);
                if (part.contains(player)) {
                    isEdited = true;
                    ClickEvent clickEvent = TextHelper.getFullClickEvent(t);
                    HoverEvent hoverEvent = TextHelper.getFullHoverEvent(t);
                    Text newText = TextHelper.stringToText(
                            part.replace(player, colorForUsername + player),
                            clickEvent,
                            hoverEvent
                    );
                    rebuilt.append(newText);
                } else {
                    rebuilt.append(t);
                }
            }

            if (isEdited) {
                Chat.chat(rebuilt);
                System.out.println("Edited message: " + TextHelper.getFormattedString(rebuilt));
                return true;
            }
            return false;
        });



















    }

}