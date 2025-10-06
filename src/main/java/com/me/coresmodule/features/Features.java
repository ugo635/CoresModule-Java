package com.me.coresmodule.features;

import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;

import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.mc;

public class Features {
    public static void register() {
        Register.command("color", ignored -> {
            Chat.chat("§0Black: §0§lBold Black : §§00");
            Chat.chat("§1Dark Blue: §1§lBold Dark Blue : §§11");
            Chat.chat("§2Dark Green: §2§lBold Dark Green : §§22");
            Chat.chat("§3Dark Aqua: §3§lBold Dark Aqua : §§33");
            Chat.chat("§4Dark Red: §4§lBold Dark Red : §§44");
            Chat.chat("§5Dark Purple: §5§lBold Dark Purple : §§55");
            Chat.chat("§6Gold: §6§lBold Gold : §§66");
            Chat.chat("§7Gray: §7§lBold Gray : §§77");
            Chat.chat("§8Dark Gray: §8§lBold Dark Gray : §§88");
            Chat.chat("§9Blue: §9§lBold Blue : §§99");
            Chat.chat("§aGreen: §a§lBold Green : §§aa");
            Chat.chat("§bAqua: §b§lBold Aqua : §§bb");
            Chat.chat("§cRed: §c§lBold Red : §§cc");
            Chat.chat("§dLight Purple: §d§lBold Light Purple : §§dd");
            Chat.chat("§eYellow: §e§lBold Yellow : §§ee");
            Chat.chat("§fWhite: §f§lBold White : §§ff");

            Chat.chat("§kObfuscated§r : §+k (without the +)");
            Chat.chat("§lBold : §§ll");
            Chat.chat("§mStrikethrough : §§mm");
            Chat.chat("§nUnderline : §§nn");
            Chat.chat("§oItalic : §§oo");
            Chat.chat("§rReset : §§rr");
        });

        Register.onChatMessage(message -> {
            String msg = Helper.removeFormatting(message.getString());
            if (msg.equals("SPOOKY! A Trick or Treat Chest has appeared!")) {
                Chat.chat("§6§l[Cm] Sooky Chest!");
                Helper.showTitle("§6§lSpooky Chest", "", 0, 25, 35);
            }
        });

        Pattern auctionPattern = Pattern.compile("^(You purchased|Visit the Auction House).*");

        Register.onChatMessageCancelable(auctionPattern, (message, matcher) -> {
            String plain = Helper.removeFormatting(message.getString());
            Chat.clickableChat(plain, "&eClick To Open The AH", "/ah");
            return true; // Cancels the Original message
        });

        Register.command("clear", ignore -> {
            mc.inGameHud.getChatHud().clear(true);
        });
        
    }
}
