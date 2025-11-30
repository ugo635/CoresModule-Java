package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.overlay.Overlay;
import com.me.coresmodule.utils.render.overlay.OverlayTextLine;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.mc;
import static com.me.coresmodule.settings.categories.Diana.MfOverlay;


public class NewMfCalc {
    public static double additionalMf = 0;
    public static boolean shuriken = false;
    public static double armorMf = 0;
    public static double heldItemMf = 0;
    public static double legion = 0;
    public static double renownedMf = 0;
    public static double kcBuff = 0;
    public static double defaultMf = 0;
    public static String profileId = "";
    public static void register() {

        // Create overlay
        Overlay overlay = new Overlay("MfOverlay", 10.0f, 10.0f, 1.0f, List.of("Chat screen", "Crafting"));
        OverlayTextLine overlayText = new OverlayTextLine("");
        overlay.register();
        overlay.setCondition(() -> MfOverlay.get());
        overlay.addLine(overlayText);


        try {
            FilesHandler.createFile("apiToken.txt");
            FilesHandler.createFile("profileId.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Register.onTick(10,args -> {
            if (mc.player == null || mc.world == null) return;
            armorMf = MfCalcHelper.mfFromArmor();
            heldItemMf = MfCalcHelper.mfFromHand();
            legion = MfCalcHelper.playersInLegion();
            renownedMf = MfCalcHelper.renownedAmt();

            double totalMf = (additionalMf + armorMf + heldItemMf)
                    * (1 + 0.35 * legion)
                    * (1 + (0.01 * renownedMf))
                    * (1 + (0.01 * kcBuff));

            overlayText.text = "§bAdditional Magic Find: §d" + totalMf;

            // additionalMf = TODO: Do The Math; TODO: Be Mf; TODO: Overlay
            // For Be, do List.forEach and do Pattern.compile("minos_hunter_\d+") Matcher.getMessage() to grab keys and do the sum of all values
        });

        Register.command("allStats", args -> {
            Chat.chat("§eArmor MF: §6" + armorMf);
            Chat.chat("§eHeld Item MF: §6" + heldItemMf);
            Chat.chat("§eLegion MF: §6" + legion);
            Chat.chat("§eKill Combo MF: §6" + kcBuff);
            Chat.chat("§eRenowned Mf: §6" + renownedMf);
            double totalMf = additionalMf + armorMf + heldItemMf + legion + renownedMf;
            Chat.chat("§aTotal MF: §6" + totalMf + "§a vs " + additionalMf);
        });

        Register.onChatMessage(Pattern.compile("§8Profile ID: ([0-9a-fA-F]+(-[0-9a-fA-F]+)+)"), false, (msg, matcher) -> {
            profileId = matcher.group(1);
            Chat.chat("§a[Cm] §eProfile ID set to: §6" + profileId);
            try {
                FilesHandler.writeToFile("profileId.txt", profileId);
            } catch (IOException e) {
                Chat.chat("§c[Cm] §eFailed to save Profile ID to file!");
                throw new RuntimeException(e);
            }
        });

        Register.onChatMessage(Pattern.compile("^.*§[a-f0-9]§l\\+(5|15|25|50) Kill Combo §b\\+3% §b✯ Magic Find$"), false, (msg, matcher) -> {
            kcBuff = switch (matcher.group(1)) {
                case "5" -> 3;
                case "15" -> 6;
                case "25" -> 9;
                case "50" -> 12;
                default -> 0;
            };
        });

        Register.onChatMessage(Pattern.compile("^§cYour Kill Combo has expired! You reached a [1-9]\\d* Kill Combo!$"), false, (msg, matcher) -> {
            kcBuff = 0;
        });

        Register.command("setAPIkey", args -> {
           String key = args[0];
            try {
                FilesHandler.writeToFile("apiToken.txt", key);
                Chat.chat("§aAPI Key set to: §e" + key);
            } catch (IOException e) {
                Chat.chat("§cFailted to set API Key: §e" + key);
                throw new RuntimeException(e);
            }
        });
    }
}