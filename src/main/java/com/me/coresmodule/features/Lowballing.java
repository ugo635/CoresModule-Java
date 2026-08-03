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

import java.io.IOException;
import java.util.*;

public class Lowballing {
    public static double amount = 0;
    public static List<String> history = new ArrayList<>();
    public static String lowballingMessage = "";

    public static void register() {
        load();

        Register.onChatMessage(msg -> {
            if (!TextHelper.containsFormattedStringOf(msg, "§b[SkyBlock]", "§eis visiting §aYour Island§e!")) return;
            Helper.showTitle("§6§lVisit", "", 5, 20, 5);
            SoundHandler.playCustomSound("ding");
        });

        // /lowball <ADD|SEE|REMOVE> <amount>
        lowballAddHistoryRemoveSee();

        // For the /lowball SEE
        lowballSee();

        // For /lowball HISTORY
        // Only shows the last 10
        lowballHistory();

        // For /lowball HISTORY SEE
        // Only shows the last 10
        lowballHistorySee();

        // For /lowball HISTORY <amount>
        lowballHistoryAmount();

        // For /lowball HISTORY ALL
        lowballHistoryAll();

        // For /lowball HISTORY COUNT
        lowballHistoryCount();

        // For /lowball SETCOPY
        lowballSetCopy();

        //For /lowball COPY
        lowballCopy();

    }

    private static void lowballSetCopy() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, buildContext) -> {
            dispatcher.register(
                    ClientCommands.literal("lowball")
                            .then(ClientCommands.literal("SETCOPY")
                                    .then(ClientCommands.argument("value", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            String value = StringArgumentType.getString(context, "value");
                                            Chat.chat("§aSet lowballing message to: §e" + value);
                                            lowballingMessage = value;
                                            save();
                                            return 1;
                                        })
                            ))
            );
        }));
    }

    private static void lowballCopy() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, buildContext) -> {
            dispatcher.register(
                    CommandHelper.literals(context -> {
                        if (lowballingMessage.isEmpty()) {
                            Chat.chat("§cNo message set! Use /lowball SETCOPY <message> to set a message.");
                            return 1;
                        }

                        Helper.copyToClipboard(lowballingMessage);
                        Chat.chat("§aCopied to clipboard: §e" + lowballingMessage);
                        return 1;
                    }, "lowball", "COPY")
            );
        }));
    }

    private static void lowballAddHistoryRemoveSee() {
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
                                                            save();
                                                            Chat.chat("§bYou made %s%.2fm §bprofit".formatted(amount >= 0 ? "§a" : "§c", amount));
                                                        }

                                                        case "REMOVE" -> {
                                                            amount -= Double.parseDouble(value);
                                                            history.addFirst("§c- " + Double.parseDouble(value) + "m");
                                                            save();
                                                            Chat.chat("§bYou made %s%.2fm §bprofit".formatted(amount >= 0 ? "§a" : "§c", amount));
                                                        }
                                                    }

                                                    return 1;
                                                })))
                )
        );
    }

    private static void lowballSee() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(
                        CommandHelper.literals((context -> {
                            Chat.chat("§bYou made %s%.2fm §bprofit".formatted(amount >= 0 ? "§a" : "§c", amount));
                            return 1;
                        }), "lowball", "SEE")
                )
        );
    }

    private static void lowballHistory() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(
                        CommandHelper.literals((context -> {
                            if (history.isEmpty()) return 1;
                            Chat.chat("§aHistory:");
                            history
                                    .stream()
                                    .limit(10)
                                    .forEach(Chat::chat);

                            return 1;
                        }), "lowball", "HISTORY")
                )
        );
    }

    private static void lowballHistorySee() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(
                        CommandHelper.literals(context -> {
                            if (history.isEmpty()) return 1;
                            Chat.chat("§aHistory:");
                            history
                                    .stream()
                                    .limit(10)
                                    .forEach(Chat::chat);

                            return 1;
                        }, "lowball", "HISTORY", "SEE")
                )
        );
    }

    private static void lowballHistoryAmount() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    CommandHelper.literal("lowball")
                            .then(ClientCommands.literal("HISTORY")
                                    .then(ClientCommands.argument("amount", StringArgumentType.word())
                                        .executes(context -> {
                                            int limit = Integer.parseInt(StringArgumentType.getString(context, "amount"));

                                            if (history.isEmpty()) return 1;
                                            if (limit <= 0) {
                                                Chat.chat("§cLimit must be higher than 0!");
                                                return 1;
                                            }

                                            if (limit > history.size()) {
                                                Chat.chat("§cLimit is higher than the history size!");
                                                return 1;
                                            }

                                            Chat.chat("§aHistory:");
                                            history
                                                    .stream()
                                                    .limit(limit)
                                                    .forEach(Chat::chat);
                                            return 1;
                                        })))
            );
        });
    }

    private static void lowballHistoryCount() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    CommandHelper.literals(context -> {
                        Chat.chat("§aTotal entries: %d".formatted(history.size()));
                        return 1;
                    }, "lowball", "HISTORY", "COUNT")
            );
        });
    }

    private static void lowballHistoryAll() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    CommandHelper.literals(context -> {
                        if (history.isEmpty()) return 1;
                        Chat.chat("§aHistory:");
                        history.forEach(Chat::chat);
                        return 1;
                    }, "lowball", "HISTORY", "ALL")
            );
        });
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
                lowballingMessage = json.optString("lowballingMessage", "");
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
            json.put("lowballingMessage", lowballingMessage);

            FilesHandler.writeToFile("lowballing.json", json.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
