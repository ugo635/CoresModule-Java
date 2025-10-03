package com.me.coresmodule;


import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CoresModule implements ModInitializer {
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
		register();
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("mymf")
					// Branch without argument
					.executes(CoresModule::mfFunc)

					// Branch with 1 argument
					.then(CommandManager.argument("mf", StringArgumentType.string())
					.executes(CoresModule::mfFunc)

					// Branch with 2 argument
					.then(CommandManager.argument("kc", StringArgumentType.string())
					.executes(CoresModule::mfFunc))));
		});
	}

	public static int mfFunc(CommandContext<ServerCommandSource> context)  {
		String mf = null;
		String kc = null;
		try {
			mf = StringArgumentType.getString(context, "mf");
		} catch (IllegalArgumentException ignored) {}

		try {
			kc = StringArgumentType.getString(context, "kc");
		} catch (IllegalArgumentException ignored) {}
		
		context.getSource().sendFeedback(() -> Text.literal("Hi §5here §a is §b a §c color §d test §e :)"), false);

		return 1;
    }


}