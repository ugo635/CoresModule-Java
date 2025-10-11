package com.me.coresmodule.features;

import com.me.coresmodule.settings.categories.ColorReplacorSettings;
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

            String colorForUsername = ColorReplacorSettings.usernameColor.get().replace("&", "§")+ player;
            String currentRank = ColorReplacorSettings.currentRank.get().replace("&", "§") + " " + player;
            String wantedRank = ColorReplacorSettings.wantedRank.get().replace("&", "§") + " " + player;

            boolean isEdited = false;

            MutableText rebuilt = Text.empty();

            List<Text> messageSiblings = new ArrayList<>(message.getSiblings());

            int i = 0;
            int iterations = 0;
            final int maxIterations = 1000;
            while (i < messageSiblings.size() - 1 && iterations < maxIterations) {
                iterations++;
                Text elem = messageSiblings.get(i);
                Text elem2 = messageSiblings.get(i + 1);
                if (TextHelper.noActionsAndDontStartWithStyle(elem) && TextHelper.noActionsAndDontStartWithStyle(elem2)) {
                    String fused = TextHelper.getFormattedString(elem) + TextHelper.getFormattedString(elem2);
                    messageSiblings.set(i, Text.of(fused));
                    messageSiblings.remove(i + 1);
                } else i++;
            }

            if (iterations == maxIterations) {
                System.out.println("Fusion stopped after reaching max iterations (possible infinite loop).");
            }

            for (Text t : messageSiblings) {
                String part = TextHelper.getFormattedString(t);
                if (part.contains(player)) {
                    isEdited = true;
                    ClickEvent clickEvent = TextHelper.getFullClickEvent(t);
                    HoverEvent hoverEvent = TextHelper.getFullHoverEvent(t);
                    Text newText = TextHelper.stringToText(
                            part.replace(currentRank, wantedRank).replace(player, colorForUsername),
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
                return true;
            }
            return false;
        });



















    }

}