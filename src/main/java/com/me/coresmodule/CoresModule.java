package com.me.coresmodule;

import com.me.coresmodule.features.Diana.InquisitorTracker;
import com.me.coresmodule.features.Diana.MfCalc;
import com.me.coresmodule.features.Diana.MfCalcHelper;
import com.me.coresmodule.features.Diana.NewMfCalc;
import com.me.coresmodule.features.Features;
import com.me.coresmodule.features.Party;
import com.me.coresmodule.features.bot.Bot;
import com.me.coresmodule.features.priv.MainPrivate;
import com.me.coresmodule.settings.Settings;
import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.SoundHandler;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.chat.ClickActionManager;
import com.me.coresmodule.utils.chat.SimulateChat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.WaypointManager;
import com.me.coresmodule.utils.render.overlay.OverlayData;
import com.me.coresmodule.utils.render.overlay.OverlayManager;
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CoresModule implements ModInitializer {
	public static String player = MinecraftClient.getInstance().getSession().getUsername();
	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static final String MOD_ID = "coresmodule";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Resourceful Configurator instance for this mod
	public static final Configurator configurator = new Configurator(MOD_ID);


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		MfCalc.register();
		SimulateChat.register();
		ClickActionManager.register();
		Features.register();
		Party.register();
		SoundHandler.register();
		MainPrivate.register();
		WaypointManager.register();
		NewMfCalc.register();
		MfCalcHelper.register();
		OverlayData.register();
		OverlayManager.register();


		configurator.register(Settings.class);
		configurator.saveConfig(Settings.class);


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
				MinecraftClient.getInstance().send(() -> {
					MinecraftClient.getInstance().setScreen(ResourcefulConfigScreen.getFactory(MOD_ID).apply(null));
				});
				return;
			} else {
				switch (arg) {
					case "help" -> {
						Chat.getChatBreak("-", "§b");
						for (Map<String, String> cmd : commands) {
                            try {
								if (cmd.get("ph") == "") {
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

		try {
            FilesHandler.register();
        } catch (IOException e) {
			Helper.printErr("[CoresModule] CoresModule.java:139 " + e);
        }

        Bot.register();

        try {
            InquisitorTracker.register();
        } catch (IOException e) {
			Helper.printErr("[CoresModule] CoresModule.java:151 " + e);
        }
    }
}