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
import com.me.coresmodule.utils.events.EventBus.CmEvents;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.events.processor.EventProcessor;
import com.me.coresmodule.utils.render.CustomItem.CustomItemRender;
import com.me.coresmodule.utils.render.WaypointManager;
import com.me.coresmodule.utils.render.overlay.OverlayData;
import com.me.coresmodule.utils.render.overlay.OverlayManager;
import com.teamresourceful.resourcefulconfig.api.loader.Configurator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


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
		AlwaysRightSphinxQuestion.register();
		DianaFeatures.register();
		EventProcessor.register();
		CmCommands.register();
		CustomItemRender.register();
		CmEvents.register();

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
			ItemStack overrideItemFrom = ItemHelper.markNbt(ItemHelper.getHeldItem(), "CmUuid", Uuid);
			ItemStack overrideItemTo = ItemHelper.markNbt(new ItemStack(Registries.ITEM.get(Identifier.of(args[0]))), "CmUuid", Uuid);
			boolean overrideItemToGlintBool = false;
			if (args.length == 2) {
				overrideItemToGlintBool = Boolean.parseBoolean(args[1]);
				overrideItemTo = ItemHelper.addMarkNbt(overrideItemTo, "CmGlint", overrideItemToGlintBool);
				overrideItemFrom = ItemHelper.addMarkNbt(overrideItemFrom, "CmGlint", overrideItemToGlintBool);
			}

			overrides.put(Uuid, new Pair<>(
                    overrideItemFrom,
                    overrideItemTo
            ));

			Chat.chat("§aReplacing " + TextHelper.getUnFormattedString(overrideItemFrom.getItemName()) + " with " + TextHelper.getUnFormattedString(overrideItemTo.getName()) + " and " + (overrideItemToGlintBool ? "with " : "§cwithout §a") + "glint.");
		});

		Register.command("copyNbt", args -> {
			NbtElement nbt = ItemHelper.encodeItemStack(ItemHelper.getHeldItem());
			mc.keyboard.setClipboard(new JSONObject(ItemHelper.getNbtMap(nbt, ItemHelper.getHeldItem())).toString(4));
			Chat.chat("§aCopied NBT HashCode to clipboard");

			Chat.chat("§aUUID: §c" + ItemHelper.getUUID(ItemHelper.getHeldItem()));
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