package com.me.coresmodule.features.Diana;

import com.me.coresmodule.settings.categories.Diana;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Pattern;

public class AlwaysRightSphinxQuestion {
    public static List<String> correctAnswers = List.of(
            "Slime",
            "Shark",
            "Mushroom Desert",
            "Roddy",
            "Ruby",
            "Divine",
            "Dark Auction",
            "Hoppity",
            "7",
            "Zombie",
            "Vacuum",
            "Marigold",
            "Prismite",
            "Junk",
            "Backwater Bayou"
    );

    public static Text aText, bText, cText;
    public static int r = -1;
    public static boolean timerOn = false;

    public static void register() {
        Register.onChatMessageCancelable(
            Pattern.compile("^§7 {3}([ABC])\\) §f(.*?)$"),
            (msg, matcher) -> {
                if (!Diana.SphinxQuestion.get()) return false;
                timer();
                String possibleAnswer = matcher.group(2).trim();
                msg = TextHelper.stringToText(
                        TextHelper.getFormattedString(msg).replaceAll("§f", correctAnswers.contains(possibleAnswer) ? "§a" : "§c"),
                        TextHelper.stringToClickEvent(""),
                        TextHelper.getFullHoverEvent(msg)
                );
                switch (matcher.group(1)) {
                    case "A" -> {
                        r = correctAnswers.contains(possibleAnswer) ? 0 : r;
                        aText = msg;
                    }
                    case "B" -> {
                        r = correctAnswers.contains(possibleAnswer) ? 1 : r;
                        bText = msg;
                    }
                    case "C" -> {
                        r = correctAnswers.contains(possibleAnswer) ? 2 : r;
                        cText = msg;
                    }
                }

                return true;
            }
        );

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof ChatScreen && Diana.SphinxQuestion.get()) {
                ScreenMouseEvents.beforeMouseClick(screen).register((s, mouseX, mouseY, button) -> {
                    if (button == 0 && r != -1) {
                        Chat.command("/sphinxanswer " + r);
                        r = -1;
                    }
                });
            }
        });
    }

    public static void timer() {
        if (timerOn) return;
        timerOn = true;
        Helper.sleep(100, () -> {
            for (Text msg : new Text[] {aText, bText, cText}) {
                Chat.chat(
                        TextHelper.stringToText(
                                TextHelper.getFormattedString(msg),
                                TextHelper.stringToClickEvent("/sphinxanswer " + r),
                                TextHelper.getFullHoverEvent(msg)
                        )
                );
            }
           timerOn = false;
        });
    }
}
