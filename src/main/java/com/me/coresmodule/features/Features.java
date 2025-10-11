package com.me.coresmodule.features;

import com.me.coresmodule.settings.categories.General;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;

import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.mc;

public class Features {
    public static void register() {
        Register.command("color", ignored -> {
            Chat.chat("§0Black: §0§lBold Black : 0");
            Chat.chat("§1Dark Blue: §1§lBold Dark Blue : 1");
            Chat.chat("§2Dark Green: §2§lBold Dark Green : 2");
            Chat.chat("§3Dark Aqua: §3§lBold Dark Aqua : 3");
            Chat.chat("§4Dark Red: §4§lBold Dark Red : 4");
            Chat.chat("§5Dark Purple: §5§lBold Dark Purple : 5");
            Chat.chat("§6Gold: §6§lBold Gold : 6");
            Chat.chat("§7Gray: §7§lBold Gray : 7");
            Chat.chat("§8Dark Gray: §8§lBold Dark Gray : 8");
            Chat.chat("§9Blue: §9§lBold Blue : 9");
            Chat.chat("§aGreen: §a§lBold Green : a");
            Chat.chat("§bAqua: §b§lBold Aqua : b");
            Chat.chat("§cRed: §c§lBold Red : c");
            Chat.chat("§dLight Purple: §d§lBold Light Purple : d");
            Chat.chat("§eYellow: §e§lBold Yellow : e");
            Chat.chat("§fWhite: §f§lBold White : f");

            Chat.chat("§kObfuscated§r : k");
            Chat.chat("§lBold : l");
            Chat.chat("§mStrikethrough : m");
            Chat.chat("§nUnderline : n");
            Chat.chat("§oItalic : o");
            Chat.chat("§rReset : r");
        });

        Register.onChatMessage(message -> {
            String msg = message.getString();
            if (msg.equals("SPOOKY! A Trick or Treat Chest has appeared!") && General.spookyChest.get()) {
                Chat.chat("§6§l[Cm] Sooky Chest!");
                Helper.showTitle("§6§lSpooky Chest", "", 0, 25, 35);
            }
        });

        Register.onChatMessageCancelable(Pattern.compile("(You purchased|Visit the Auction House).*"), (message, matcher) -> {
            if (General.ahMsg.get()) {
                Chat.clickableChat(TextHelper.formattedString(message), "§eClick To Open The AH", "/ah");
                return true; // Cancels the Original message
            } else {
                return false;
            }
        });

        Register.command("clear", ignore -> {
            mc.inGameHud.getChatHud().clear(true);
        });
    }
}
