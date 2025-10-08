package com.me.coresmodule;


import com.me.coresmodule.features.Features;
import com.me.coresmodule.features.Party;
import com.me.coresmodule.settings.CMSettings;
import com.me.coresmodule.settings.ModMenuInterop;
import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.chat.ClickActionManager;
import com.me.coresmodule.utils.events.Register;
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import net.fabricmc.api.ModInitializer;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;

import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.me.coresmodule.features.Diana.InquisitorTracker;
import com.me.coresmodule.features.Diana.mfCalc;
import com.me.coresmodule.utils.chat.simulateChat;

import java.io.IOException;

public class CoresModule implements ModInitializer {
	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static final String MOD_ID = "coresmodule";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Resourceful Configurator instance for this mod
	public static final Configurator CONFIGURATOR = new Configurator(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		mfCalc.register();
		simulateChat.register(); // TODO: Make it /cm simulateChat
		ClickActionManager.register();
		Features.register();
		Party.register();

		CONFIGURATOR.register(CMSettings.class);

		// Register the "/cm" command to open the config screen
		Register.command("cm", arg -> {
			var factory = ResourcefulConfigScreen.getFactory("CMSettings");

			MinecraftClient.getInstance().setScreen(factory.apply(null));
		});

		try {
            FilesHandler.register();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            InquisitorTracker.register();
        } catch (IOException e) {
			System.err.println("[CoresModule] CoresModule.java:29 " + e);
        }

    }
}