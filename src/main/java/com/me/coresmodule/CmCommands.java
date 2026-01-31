package com.me.coresmodule;

import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import net.minecraft.client.MinecraftClient;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.me.coresmodule.CoresModule.MOD_ID;
import static com.me.coresmodule.CoresModule.mc;

public class CmCommands {
    public static void register() {
        ArrayList<Map<String, String>> commands = new ArrayList<>();
        commands.add(new HashMap<>() {{
            put("cmd", "cm");
            put("description", "Open the settings");
            put("ph", "cm");
        }});
        commands.add(new HashMap<>() {{
            put("cmd", "cm help");
            put("description", "Show this message");
            put("ph", "cm help");
        }});
        commands.add(new HashMap<>() {{
            put("cmd", "mymf <Mf>");
            put("description", "Gives your mf on inquisitors -> §cSee /mf_help for details on the input");
            put("ph", "mymf 300");
        }});
        commands.add(new HashMap<>() {{
            put("cmd", "mymf <Mf> <Mf From Kill Combo>");
            put("description", "Gives your mf on inquisitors: §cSee /mfCombo_help for details on the input");
            put("ph", "mymf 300 6");
        }});

        // Register the "/cm" command to open the config screen
        Register.command("cm", args -> {
            String arg;
            if (args.length > 0) arg = args[0].toLowerCase();
            else arg = "";

            if (arg.equals("config") || arg.equals("settings") || arg.isEmpty()) {
                mc.send(() -> {
                    mc.setScreen(ResourcefulConfigScreen.getFactory(MOD_ID).apply(null));
                });
                return;
            } else {
                // For stuff that isn't opening the config screen
                switch (arg) {
                    case "help" -> {
                        Chat.getChatBreak("-", "§b");
                        for (Map<String, String> cmd : commands) {
                            try {
                                if (Objects.equals(cmd.get("ph"), "")) {
                                    Chat.clickableChat(
                                            "§7> §a/" + cmd.get("cmd") + " §7- §e" + cmd.get("description"),
                                            "§eClick to run /" + cmd.get("cmd"),
                                            "/" + cmd.get("cmd"),
                                            "RunCommand"
                                    );
                                } else {
                                    Chat.clickableChat(
                                            "§7> §a/" + cmd.get("cmd") + " §7- §e" + cmd.get("description"),
                                            "§eClick to run /" + cmd.get("cmd"),
                                            "/" + cmd.get("ph"),
                                            "SuggestCommand"
                                    );
                                }

                            } catch (URISyntaxException e) {
                                throw new RuntimeException(e);
                            }
                            Chat.getChatBreak("-", "§b");
                        }
                    }
                    default -> {
                        Chat.chat("§c[Cm] Unknown command. Use /cm help for a list of commands");
                    }
                }
            }
        });
    }
}
