package com.me.coresmodule;

import com.me.coresmodule.features.Diana.*;
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
import com.me.coresmodule.utils.events.processor.EventProcessor;
import com.me.coresmodule.utils.render.WaypointManager;
import com.me.coresmodule.utils.render.overlay.OverlayData;
import com.me.coresmodule.utils.render.overlay.OverlayManager;
import com.me.coresmodule.utils.ScreenshotUtils;
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;

public class CoresModule implements ModInitializer {
	public static String player = MinecraftClient.getInstance().getSession().getUsername();
	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static final String MOD_ID = "coresmodule";
	public static Item overrideItemFrom = null;
	public static Item overrideItemTo = null;

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

		try {
			FilesHandler.register();
		} catch (IOException e) {
			Helper.printErr("[CoresModule] CoresModule.java:139 " + e);
		}

		Bot.register();

		try {
			RareDropTracker.register();
		} catch (IOException e) {
			Helper.printErr("[CoresModule] CoresModule.java:151 " + e);
		}

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
		AlwaysRightSphinxQuestion.register();
		DianaFeatures.register();
		EventProcessor.register();
		CmCommands.register();

		configurator.register(Settings.class);
		configurator.saveConfig(Settings.class);

		Register.command("replaceItem", args -> {
			Identifier id1 = Identifier.of(args[0]);
			Identifier id2 = Identifier.of(args[1]);
			overrideItemFrom = Registries.ITEM.get(id1);
			overrideItemTo = Registries.ITEM.get(id2);

			Chat.chat("§aReplacing " + id1 + " with " + id2);
		});

		/*
		Register.command("copyToClip", args -> {
			mc.keyboard.setClipboard("Hii");
		});

		Register.command("copyToClipImage", args -> {
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            ScreenshotUtils.copyImageToClipboard(image);
		});
		 */
    }
}