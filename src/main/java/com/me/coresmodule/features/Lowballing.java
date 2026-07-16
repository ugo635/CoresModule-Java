package com.me.coresmodule.features;

import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.SoundHandler;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.helpers.CommandHelper;
import com.me.coresmodule.utils.helpers.Helper;
import com.me.coresmodule.utils.helpers.TextHelper;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Lowballing {
    public static double amount = 0;
    public static List<String> history = new ArrayList<>();

    public static void register() {
        load();

        Register.onChatMessage(msg -> {
            if (!TextHelper.containsFormattedStringOf(msg, "§b[SkyBlock]", "§eis visiting §aYour Island§e!")) return;
            Helper.showTitle("§6§lVisit", "", 5, 20, 5);
            SoundHandler.playCustomSound("ding");
        });

        // /lowball <ADD|SEE|REMOVE> <amount>
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(
                        ClientCommands.literal("lowball") // Command name
                                .then(CommandHelper.argWithOptions("type", List.of("ADD", "REMOVE", "SEE"))
                                        .then(ClientCommands.argument("value", StringArgumentType.word())
                                                .executes(context -> {
                                                    String type = StringArgumentType.getString(context, "type");
                                                    String value = StringArgumentType.getString(context, "value");

                                                    switch (type) {
                                                        case "ADD" -> {
                                                            amount += Double.parseDouble(value);
                                                            history.addFirst("§a+ " + Double.parseDouble(value) + "m");
                                                            if (history.size() >= 11) history.removeLast();
                                                            save();
                                                            Chat.chat("§bYou made %s%.2fm §bprofit".formatted(amount >= 0 ? "§a" : "§c", amount));
                                                        }

                                                        case "REMOVE" -> {
                                                            amount -= Double.parseDouble(value);
                                                            history.addFirst("§c- " + Double.parseDouble(value) + "m");
                                                            if (history.size() >= 11) history.removeLast();
                                                            save();
                                                            Chat.chat("§bYou made %s%.2fm §bprofit".formatted(amount >= 0 ? "§a" : "§c", amount));
                                                        }
                                                    }

                                                    return 1;
                                                })))
                )
        );

        // For the /lowball SEE
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(
                        ClientCommands.literal("lowball")
                                .then(ClientCommands.literal("SEE")
                                        .executes(context -> {
                                            Chat.chat("§bYou made %s%.2fm §bprofit".formatted(amount >= 0 ? "§a" : "§c", amount));
                                            return 1;
                                        }))
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(
                        ClientCommands.literal("lowball")
                                .then(ClientCommands.literal("HISTORY")
                                        .executes(context -> {
                                            if (history.isEmpty()) return 1;
                                            Chat.chat("§aHistory:");
                                            history.forEach(Chat::chat);
                                            return 1;
                                        }))
                )
        );
    }

    private static void load() {
        try {
            FilesHandler.createFile("lowballing.json");
            String content = FilesHandler.getContent("lowballing.json");

            if (content.trim().isEmpty()) {
                save();
                return;
            }

            JSONObject json = new JSONObject(content);
            if (!json.isEmpty()) {
                amount = json.optDouble("amount", 0.0);

                if (json.has("history")) {
                    JSONArray jsonArray = json.getJSONArray("history");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        history.add(jsonArray.getString(i));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void save() {
        try {
            JSONObject json = new JSONObject();
            json.put("amount", amount);
            json.put("history", history);

            FilesHandler.writeToFile("lowballing.json", json.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
