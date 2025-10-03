package com.me.coresmodule;


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



	public static void createCommand(String name, CommandContext<ServerCommandSource> context) {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal(name)
					.executes(CoresModule::context)
			);
		});
	}

	public static void register() {
		/*
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("mymf")

					// Branch with 2 argument
					.then(CommandManager.argument("mf", StringArgumentType.string()))
					.then(CommandManager.argument("kc", StringArgumentType.string())
							.executes(context -> {
								String mf = StringArgumentType.getString(context, "mf"); // Get the value of the arg 'mf'
								String kc = StringArgumentType.getString(context, "mf"); // Get the value of the arg 'kc'

								context.getSource().sendFeedback(() -> Text.literal("You put §5" + mf + "§r as an argument and " + kc), false);
								LOGGER.info("Magic Find: " + mf + "\nKill Combo Percentage:§5 " + kc);
								return 1;
							})
					)

					// Branch with 1 argument
					.then(CommandManager.argument("mf", StringArgumentType.string())
							.executes(context -> {
								String mf = StringArgumentType.getString(context, "mf"); // use argument name "mf"
								context.getSource().sendFeedback(() -> Text.literal("You put " + mf + " as an argument"), false);
								LOGGER.info("Magic Find: " + mf);
								return 1;
							})
					)


					// Branch without argument
					.executes(context -> {
						context.getSource().sendFeedback(() -> Text.literal("You need to put an argument"), false);
						return 1;
					})
			);
		});
		*/


	}
}