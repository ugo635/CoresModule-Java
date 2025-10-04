package com.me.coresmodule;


import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.me.coresmodule.features.Diana.mfCalc;
import com.me.coresmodule.features.Diana.InquisitorDetector;
import com.me.coresmodule.utils.chat.simulateChat;

public class CoresModule implements ModInitializer {
	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static final String MOD_ID = "coresmodule";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		mfCalc.register();
		simulateChat.register(); // TODO: Make it /cm simulateChat
		InquisitorDetector.register();
	}
}