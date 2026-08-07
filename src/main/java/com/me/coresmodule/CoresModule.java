package com.me.coresmodule;

import com.me.coresmodule.features.*;
import com.me.coresmodule.features.bot.Bot;
import com.me.coresmodule.features.diana.DianaFeatures;
import com.me.coresmodule.features.farming.HoldDirection;
import com.me.coresmodule.features.farming.Orientation;
import com.me.coresmodule.features.farming.PestCooldown;
import com.me.coresmodule.settings.Settings;
import com.me.coresmodule.utils.*;
import com.me.coresmodule.utils.chat.ClickActionManager;
import com.me.coresmodule.utils.chat.SimulateChat;
import com.me.coresmodule.utils.events.impl.CmEventReg;
import com.me.coresmodule.utils.events.processor.EventProcessor;
import com.me.coresmodule.utils.render.WaypointManager;
import com.me.coresmodule.utils.render.gui.GUIs;
import com.me.coresmodule.utils.render.hud.overlay.OverlayData;
import com.me.coresmodule.utils.render.hud.overlay.OverlayManager;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CoresModule implements ModInitializer {
	public static String player = Minecraft.getInstance().getUser().getName();
	public static Minecraft mc = Minecraft.getInstance();
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
		org.apache.logging.log4j.core.config.Configurator.setLevel("com.mojang.authlib", Level.OFF);
		((org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false)).updateLoggers(); // Removes errors when joining hypixel

		TryCatch.register();
		Bot.register();
		SimulateChat.register();
		ClickActionManager.register();
		Features.register();
		Party.register();
		SoundHandler.register();
		WaypointManager.register();
		OverlayData.register();
		OverlayManager.register();
		DianaFeatures.register();
		EventProcessor.register();
		CmCommands.register();
		CmEventReg.register();
		GUIs.register();
		HoldDirection.register();
		Orientation.register();
		TabList.register();
		PestCooldown.register();
		CenturyRaffle.register();
		Lowballing.register();
		CritterSafari.register();

		configurator.register(Settings.class);
		configurator.saveConfig(Settings.class);


		/*
		Register.command("copyToClip", args -> {
			mc.keyboardHandler.setClipboard("");
		});

		Register.command("copyToClipImage", args -> {
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            ScreenshotUtils.copyImageToClipboard(image);
		});
		 */
    }
}