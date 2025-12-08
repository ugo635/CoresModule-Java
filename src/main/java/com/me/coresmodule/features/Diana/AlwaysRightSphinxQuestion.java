package com.me.coresmodule.features.Diana;

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

    public static List<String> wrongAnswers = List.of(
            "Hipppity",
            "Beppity",
            "Whale",
            "Snake",
            "Sheep",
            "Slug",
            "Rare",
            "Legendary",
            "Enderman",
            "Spider",
            "Amethyst",
            "Peridot",
            "Topaz",
            "Jade",
            "Spider’s Den",
            "The Park",
            "The Hub",
            "Fishing",
            "The End",
            "Axe",
            "Hoe",
            "Dyes",
            "Pests",
            "King Midas",
            "Gemma",
            "Remi",
            "Rudy",
            "6",
            "9"
    );

    public static boolean a, b, c;
    public static int r = -1;

    public static void register() {
        a = false;
        b = false;
        c = false;
        r = -1;

        Register.onChatMessageCancelable(
            Pattern.compile("^§7   ([ABC])\\) §f(.*?)$"),
            (msg, matcher) -> {
                String letter = matcher.group(1);
                String possibleAnswer = matcher.group(2).trim();
                switch (letter) {
                    case "A" -> a = correctAnswers.contains(possibleAnswer);
                    case "B" -> b = correctAnswers.contains(possibleAnswer);
                    case "C" -> c = correctAnswers.contains(possibleAnswer);
                }

                if (a) r = 0;
                else if (b) r = 1;
                else r = 2;

                Text finalMsg = TextHelper.stringToText(
                        TextHelper.getFormattedString(msg),
                        TextHelper.stringToClickEvent("/sphinxanswer " + r),
                        TextHelper.getFullHoverEvent(msg)
                );

                if (letter.equals("A")) {
                    if (r == 0) return false;
                    else {
                        Chat.chat(
                                TextHelper.stringToText(
                                        TextHelper.getFormattedString(msg),
                                        TextHelper.stringToClickEvent("/sphinxanswer " + 2), // 1/2 chance for it to be right if A isn't the answer
                                        TextHelper.getFullHoverEvent(msg)
                                )
                        );
                        return true;
                    }
                } else if (letter.equals("B")) {
                    if (r == 0) {
                        Chat.chat(finalMsg);
                    } else if (r == 1) {
                        return false;
                    } else {
                        Chat.chat(
                                TextHelper.stringToText(
                                        TextHelper.getFormattedString(msg),
                                        TextHelper.stringToClickEvent("/sphinxanswer " + 2),
                                        TextHelper.getFullHoverEvent(msg)
                                )
                        );
                    }
                } else {
                    if (r == 2) return false;
                    Chat.chat(finalMsg);
                }

                return true;
            }
        );
    }

    public static List<String> getWrongAnsFromRightOne(String wrong) {
        return switch (wrong) {
            case "Slime" -> List.of("Sheep", "Slug");
            case "Shark" -> List.of("Whale", "Snake");
            case "Mushroom Desert" -> List.of("The Hub", "The Park");
            case "Roddy" -> List.of("Remi", "Ruddy");
            case "Ruby" -> List.of("Topaz", "Jade");
            case "Prismite" -> List.of("Peridot", "Amethyst");
            case "Divine" -> List.of("Legendary", "Rare");
            case "Dark Auction" -> List.of("Fishing", "The End");
            case "Hoppity" -> List.of("Hipppity", "Beppity");
            case "7" -> List.of("6", "9");
            case "Zombie" -> List.of("Enderman", "Spider");
            case "Vacuum" -> List.of("Axe", "Hoe");
            case "Marigold" -> List.of("King Midas", "Gemma");
            case "Junk" -> List.of("Dyes", "Pests");
            case "Backwater Bayou" -> List.of("The Park", "Spider's Den");
            default -> List.of();
        };
    }

    public static String getRightAnsFromWrongOne(String right) {
        return switch (right) {
            case "Sheep", "Slug" -> "Slime";
            case "Whale", "Snake" -> "Shark";
            case "The Hub", "The Park" -> "Mushroom Desert";
            case "Remi", "Ruddy" -> "Roddy";
            case "Topaz", "Jade" -> "Ruby";
            case "Peridot", "Amethyst" -> "Prismite";
            case "Legendary", "Rare" -> "Divine";
            case "Fishing", "The End" -> "Dark Auction";
            case "Hipppity", "Beppity" -> "Hoppity";
            case "6", "9" -> "7";
            case "Enderman", "Spider" -> "Zombie";
            case "Axe", "Hoe" -> "Vacuum";
            case "King Midas", "Gemma" -> "Marigold";
            case "Dyes", "Pests" -> "Junk";
            case "Spider's Den" -> "Backwater Bayou";
            default -> null;
        };
    }
}
