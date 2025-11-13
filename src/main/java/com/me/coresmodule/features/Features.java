package com.me.coresmodule.features;

import com.me.coresmodule.settings.categories.General;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.SoundHandler;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.settings.categories.Diana;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.MOD_ID;
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

        Register.onChatMessageCancelable(Pattern.compile("(You purchased|Visit the Auction House).*"), false, (message, matcher) -> {
            if (General.ahMsg.get()) {
                Chat.clickableChat(TextHelper.formattedString(message), "§eClick To Open The AH", "/ah");
                return true;
            } else {
                return false;
            }
        });

        Register.onChatMessageCancelable(message -> {
            return General.hideHoppityHunt.get() && TextHelper.formattedString(message).contains("§dHoppity's Hunt §ehas begun! Help §aHoppity §efind his §6Chocolate Rabbit Eggs §eacross SkyBlock each day during the §aSpring§e!");
        });

        Register.command("clear", ignore -> {
            mc.inGameHud.getChatHud().clear(true);
        });

        Register.onChatMessage(message -> {
            if (General.pickaceAbility.get() && TextHelper.formattedString(message).contains("§aYou used your §6Maniac Miner §aPickaxe Ability!")) { // TODO make it compatible with other pickaxe abilities
                Helper.exactSleep(103000, () -> {
                    Chat.chat("§6§l[Cm] Pickaxe Ability Ready!");
                    Helper.showTitle("§6§lPickaxe Ability", "§aReady!", 0, 25, 35);
                });
            }
        });

        Register.command("ftax", player -> {
            if (player.length > 0) {
                if (player.length == 1) {
                    Chat.command("pc Sry bud, friend tax!");
                    Helper.sleep(1000, () -> {
                        Chat.command("p kick " + player[0]);
                    });
                } else if (player.length == 2) {
                    Chat.command("pc Sry bud, friend tax!");
                    Helper.sleep(1000, () -> {
                        Chat.command("p kick " + player[0]);
                    });
                    Helper.sleep(2000, () -> {
                        Chat.command("p " + player[1]);
                    });
                }
            }

        });

        Register.onChatMessage(msg -> {
            if (TextHelper.formattedString(msg).contains("§eYou dug out a §2Minotaur§e!") && Diana.minotaurOnScreen.get()) Helper.showTitle("§c§lMinotaur", "", 0, 25, 35);
        });

        Register.onChatMessage(msg -> {
            if (TextHelper.formattedString(msg).contains("§6§lRARE DROP! §eYou dug out a §9Mythos Fragment§e!") && Diana.announceMythosFrag.get()) {
                Chat.command("pc RARE DROP! You dug out a Mythos Fragment!");
            };
        });

        Register.onChatMessage(msg -> {
            if (TextHelper.formattedString(msg).contains("§eYou need to equip a §d§lMYTHIC §egriffin pet to fight this!") && Diana.wrongPet.get()) Helper.showTitle("§4§l Wrong Pet", "", 0, 20, 20);
        });

        Register.onChatMessage(Pattern.compile("^(?<channel>.*> )?(?<playerName>.+?)[§&]f: (?:[§&]r)?x: (?<x>[^ ,]+),? y: (?<y>[^ ,]+),? z: (?<z>[^ ,]+)(?<trailing>.*)$"),false, (msg, result) -> {
            Chat.chat("§c[CoresModule] Coords Delected");
            SoundHandler.playSound("emergencymeeting");
        });
    }
}
