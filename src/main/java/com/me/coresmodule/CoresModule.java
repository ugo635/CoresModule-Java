package com.me.coresmodule;

import com.me.coresmodule.features.Diana.*;
import com.me.coresmodule.features.Features;
import com.me.coresmodule.features.Party;
import com.me.coresmodule.features.bot.Bot;
import com.me.coresmodule.features.priv.MainPrivate;
import com.me.coresmodule.settings.Settings;
import com.me.coresmodule.utils.*;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.chat.ClickActionManager;
import com.me.coresmodule.utils.chat.SimulateChat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.events.processor.EventProcessor;
import com.me.coresmodule.utils.render.CustomItemRender;
import com.me.coresmodule.utils.render.WaypointManager;
import com.me.coresmodule.utils.render.overlay.OverlayData;
import com.me.coresmodule.utils.render.overlay.OverlayManager;
import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.component.DataComponentTypes;


import javax.imageio.ImageIO;

import static com.me.coresmodule.utils.render.CustomItemRender.CmGlint;


public class CoresModule implements ModInitializer {
	public static String player = MinecraftClient.getInstance().getSession().getUsername();
	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static final String MOD_ID = "coresmodule";
	public static HashMap<String, Pair<ItemStack, ItemStack>> overrides = new HashMap<>();

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
		CustomItemRender.register();

		configurator.register(Settings.class);
		configurator.saveConfig(Settings.class);

		/*
		 * Edit first & third person texture, inventory texture & hotbar texture of an item
		 * FIXME: No glint when dropped/third person
		 * FIXME: Texture no longer switched after switching the item to offhand -> Problem is uuid = null
		 */
		Register.command("replaceItem", args -> {
			if (!List.of(1, 2).contains(args.length)) {
				Chat.chat("§cUsage: /replaceItem <item_to> <true/false (glint) (optional)>");
				return;
			}

			String Uuid = String.valueOf(UUID.randomUUID());
			ItemStack overrideItemFrom = ItemHelper.getHeldItem();
			ItemStack overrideItemTo = new ItemStack(Registries.ITEM.get(Identifier.of(args[0])));
			overrideItemFrom.set(CustomItemRender.UuidComponent, Uuid);
			overrideItemTo.set(CustomItemRender.UuidComponent, Uuid);
			boolean overrideItemToGlintBool = false;
			if (args.length == 2) {
				overrideItemTo.set(CmGlint, Boolean.parseBoolean(args[1]));
				overrideItemToGlintBool = Boolean.parseBoolean(args[1]);
			}

			overrides.put(Uuid, new Pair<>(
                    overrideItemFrom,
                    overrideItemTo
            ));

			Chat.chat("§aReplacing " + TextHelper.getUnFormattedString(overrideItemFrom.getItemName()) + " with " + TextHelper.getUnFormattedString(overrideItemTo.getName()) + " and " + (overrideItemToGlintBool ? "with " : "§cwithout §a") + "glint.");
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