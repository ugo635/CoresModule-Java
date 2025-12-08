package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
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
                timer();
                String possibleAnswer = matcher.group(2).trim();
                msg = TextHelper.stringToText(
                        TextHelper.getFormattedString(msg).replaceAll("§f", "§a"),
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
