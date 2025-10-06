// Package and imports
package com.me.coresmodule.features;

import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.Helper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.MutableText;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.me.coresmodule.utils.events.Register;

public class Party {

    private static final Pattern PARTY_PATTERN = Pattern.compile("^(Party (Moderators|Leader|Members)):\\s?(.*)$", Pattern.CASE_INSENSITIVE);
    private static final List<String> partyMembers = new ArrayList<>();
    private static String playerOwner = String.valueOf(MinecraftClient.getInstance().player);

    private static Text t1, t2, t3, t4, t5, t6, t7;

    public static void onChatMessage(Text message) {
        String msg = message.getString();
        Matcher match = PARTY_PATTERN.matcher(msg);
        if (!match.find()) return;

        String[] rawPlayers = match.group(3).split("●");
        List<String> players = new ArrayList<>();
        for (String player : rawPlayers) {
            player = player
                    .replace("[VIP] ", "")
                    .replace("[VIP+] ", "")
                    .replace("[MVP] ", "")
                    .replace("[MVP+] ", "")
                    .replace("[MVP++] ", "")
                    .replace(" ", "");
            if (!player.isEmpty()) players.add(player);
        }

        for (String player : players) {
            if (!partyMembers.contains(player) && !player.equals(playerOwner)) {
                partyMembers.add(player);
            }
        }
    }

    public static void register() {

        Register.onChatMessage(message -> {
            String msg = message.getString();
            Matcher match = PARTY_PATTERN.matcher(msg);
            if (!match.find()) return;

            String[] rawPlayers = match.group(3).split("●");
            List<String> players = new ArrayList<>();
            for (String player : rawPlayers) {
                player = player
                        .replace("[VIP] ", "")
                        .replace("[VIP+] ", "")
                        .replace("[MVP] ", "")
                        .replace("[MVP+] ", "")
                        .replace("[MVP++] ", "")
                        .replace(" ", "");
                if (!player.isEmpty()) players.add(player);
            }

            for (String player : players) {
                if (!partyMembers.contains(player) && !player.equals(playerOwner)) {
                    partyMembers.add(player);
                }
            }
        });

        Register.command("fp", args -> {
            partyMembers.clear();

            Helper.sleep(100, () -> Chat.command("pl"));

            Helper.sleep(500, () -> {
                if (partyMembers.size() == 0 || partyMembers.size() > 7) return;

                for (int i = 0; i < partyMembers.size(); i++) {
                    String pMember = partyMembers.get(i);
                    if (!pMember.equals(playerOwner)) {
                        MutableText comp = Text.literal("§a" + pMember)
                                .styled(style -> style
                                        .withHoverEvent(new HoverEvent.ShowText(Text.literal("§eadd §c" + pMember)))
                                        .withClickEvent(new ClickEvent.RunCommand("/f add " + pMember))
                                );
                        switch (i) {
                            case 0 -> t1 = comp;
                            case 1 -> t2 = comp;
                            case 2 -> t3 = comp;
                            case 3 -> t4 = comp;
                            case 4 -> t5 = comp;
                            case 5 -> t6 = comp;
                            case 6 -> t7 = comp;
                        }
                    }
                }

                List<Text> components = new ArrayList<>();
                if (t1 != null) components.add(t1);
                if (t2 != null) components.add(t2);
                if (t3 != null) components.add(t3);
                if (t4 != null) components.add(t4);
                if (t5 != null) components.add(t5);
                if (t6 != null) components.add(t6);
                if (t7 != null) components.add(t7);

                if (!components.isEmpty()) {
                    Chat.chat(Chat.getChatBreak());
                    MutableText msgComp = Text.literal("§e[CM] Click the user to add!\n");
                    for (int i = 0; i < components.size(); i++) {
                        msgComp.append(components.get(i));
                        if (i != components.size() - 1) msgComp.append(Text.literal(" §c|§r "));
                    }
                    MinecraftClient.getInstance().player.sendMessage(msgComp, false);
                    Chat.chat(Chat.getChatBreak());
                }

                t1 = t2 = t3 = t4 = t5 = t6 = t7 = null;
                partyMembers.clear();
            });
        }, "fParty");


    }
}
