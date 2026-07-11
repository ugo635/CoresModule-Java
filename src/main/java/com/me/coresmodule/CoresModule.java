package com.me.coresmodule;

import com.me.coresmodule.features.Diana.*;
import com.me.coresmodule.features.Features;
import com.me.coresmodule.features.Party;
import com.me.coresmodule.features.bot.Bot;
import com.me.coresmodule.features.farming.HoldDirection;
import com.me.coresmodule.features.farming.Orientation;
import com.me.coresmodule.features.farming.PestCooldown;
import com.me.coresmodule.features.priv.MainPrivate;
import com.me.coresmodule.settings.Settings;
import com.me.coresmodule.utils.*;
import com.me.coresmodule.utils.Tuples.Quadruple;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.chat.ClickActionManager;
import com.me.coresmodule.utils.chat.SimulateChat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.events.impl.CmEventReg;
import com.me.coresmodule.utils.events.processor.EventProcessor;
import com.me.coresmodule.utils.render.WaypointManager;
import com.me.coresmodule.utils.render.gui.GUIs;
import com.me.coresmodule.utils.render.overlay.OverlayData;
import com.me.coresmodule.utils.render.overlay.OverlayManager;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


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
		TryCatch.register();
		Bot.register();
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
		DianaFeatures.register();
		EventProcessor.register();
		CmCommands.register();
		CmEventReg.register();
		GUIs.register();
		HoldDirection.register();
		Orientation.register();
		TabList.register();
		PestCooldown.register();

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