package com.me.coresmodule.features;

import com.me.coresmodule.settings.categories.Diana;
import com.me.coresmodule.settings.categories.General;
import com.me.coresmodule.utils.helpers.Helper;
import com.me.coresmodule.utils.helpers.ItemHelper;
import com.me.coresmodule.utils.SoundHandler;
import com.me.coresmodule.utils.helpers.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.mc;

public class Features {
    static List<String> toInv = new ArrayList<String>();
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
                Chat.clickableChat(TextHelper.getFormattedString(message), "§eClick To Open The AH", "/ah");
                return true;
            } else {
                return false;
            }
        });

        Register.onChatMessageCancelable(message -> {
            return General.hideHoppityHunt.get() && TextHelper.formattedString(message).contains("§dHoppity's Hunt §ehas begun! Help §aHoppity §efind his §6Chocolate Rabbit Eggs §eacross SkyBlock each day during the §aSpring§e!");
        });

        Register.command("clear", ignore -> {
            mc.gui.getChat().clearMessages(true);
        });

        Register.onChatMessage(message -> {
            if (General.pickaceAbility.get() && TextHelper.formattedString(message).contains("§aYou used your §6Maniac Miner §aPickaxe Ability!")) {
                // TODO make it compatible with other pickaxe abilities
                Helper.exactSleep(103000, () -> {
                    Chat.chat("§6§l[Cm] Pickaxe Ability Ready!");
                    Helper.showTitle("§6§lPickaxe Ability", "§aReady!", 0, 25, 35);
                });
            }
        });

        Register.command("ftax", player -> {
            if (player.length > 0) {
                if (player.length == 1) {
                    // Kick if we just wanna kick a player
                    Chat.command("pc Sry bud, friend tax!");
                    Helper.sleep(1000, () -> {
                        Chat.command("p kick " + player[0]);
                    });
                } else if (player.length == 2) {
                    // Kick the first player and invites the second
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
            if (TextHelper.formattedString(msg).contains("§6§lRARE DROP! §eYou dug out a §5Braided Griffin Feather§e!") && Diana.announceBraided.get()) {
                Chat.command("pc RARE DROP! You dug out a Mythos Fragment!");
            };
        });

        Register.onChatMessage(msg -> {
            if (TextHelper.formattedString(msg).contains("§eYou need to equip a §d§lMYTHIC §egriffin pet to fight this!") && Diana.wrongPet.get()) Helper.showTitle("§4§l Wrong Pet", "", 0, 20, 20);
        });

        Register.onChatMessage(Pattern.compile("^(?<channel>.*> )?(?<playerName>.+?)[§&]f: (?:[§&]r)?x: (?<x>[^ ,]+),? y: (?<y>[^ ,]+),? z: (?<z>[^ ,]+)(?<trailing>.*)$"),true, (msg, result) -> {
            if (!General.coordSound.get()) return;
            Chat.chat("§c[CoresModule] Coords Delected");
            SoundHandler.playCustomSound("emergencymeeting");
        });

        // ?<name> is to give name to groups to be used as Matcher.group("name")
        Register.onChatMessage(msg -> {
            if (!TextHelper.getFormattedString(msg).contains("§ehas left the party.")) return;
            if (!toInv.isEmpty()) {
                Chat.command("p " + toInv.getFirst());
                toInv.removeFirst();
            }
        });

        Register.command("inviteOnLeave", args -> {
            toInv.add(args[0]);
            Chat.chat("§6[Cm] Will invite when someone leaves party: §e" + args[0]);
        }, "partyIfLeave", "inviteIfLeave");

        Register.onChatMessageCancelable(Pattern.compile("^.*This ability is on cooldown for [0-4]s\\.$"), false, (msg, match) -> {
            return ItemHelper.getHeldItemName().contains("Atomsplit Katana");
        });

        Register.command("testGradient", args -> {
            List<List<Object>> gradient = List.of(
                    List.of("C", 0xFF9B5DE5),
                    List.of("o", 0xFFA653E0),
                    List.of("r", 0xFFB04DDC),
                    List.of("e", 0xFFB947D7),
                    List.of("s", 0xFFC341D2),
                    List.of("M", 0xFFCC3DCD),
                    List.of("o", 0xFFD43AC7),
                    List.of("d", 0xFFDC3FC1),
                    List.of("u", 0xFFE248BA),
                    List.of("l", 0xFFE852B4),
                    List.of("e", 0xFFEE5AAE)
            );

            Chat.chat(TextHelper.getGradient(gradient));
        }, "testGradient");

        Register.command("testGradient2", args -> {
            List<List<Object>> gradient = List.of(
                    List.of("[", 0xFF9B5DE5),
                    List.of("C", 0xFFA14FDF),
                    List.of("o", 0xFFA846DC),
                    List.of("r", 0xFFB03ED8),
                    List.of("e", 0xFFB937D4),
                    List.of("s", 0xFFC32FCF),
                    List.of("M", 0xFFCC3DCD),
                    List.of("o", 0xFFD53BC7),
                    List.of("d", 0xFFDC3FC1),
                    List.of("u", 0xFFE447BA),
                    List.of("l", 0xFFE94FB4),
                    List.of("e", 0xFFEC56B0),
                    List.of("]", 0xFFEE5AAE)
            );

            Chat.chat(TextHelper.getGradient(gradient));
        }, "testGradient2");

        Register.onChatMessage(text -> {
            String msg = TextHelper.getUnFormattedString(text);

            if (!msg.contains("BRRR!")) return;

            int coldInt = 0;

            if (msg.contains("It's getting really cold in here! But you've got to keep moving...")) {
                coldInt = 25;
            } else if (msg.contains("It's so cold that you can barely feel your fingers. Moving is getting difficult...")) {
                coldInt = 50;
            } else if (msg.contains("Your movement slows to a crawl as the cold threatens to take over. Time to get out of here...")) {
                coldInt = 75;
            } else if (msg.contains("You're freezing! All you can think about is getting out of here to a warm campfire...")) {
                coldInt = 90;
            } else {
                return;
            }

            String cold = "§b" + (coldInt) + "❄";

            Helper.showTitle(cold, "", 0, 25, 35);
        });

    }
}
