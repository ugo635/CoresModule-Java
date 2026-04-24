package com.me.coresmodule.utils;

import com.me.coresmodule.utils.events.Register;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.me.coresmodule.CoresModule.mc;

public class TabList {

	/** Holds cached tab lines updated each tick. */
	private static List<String> cachedTabLines = Collections.emptyList();

	/** Registers a task to update the cache each tick. */
	public static void init() {
		// Register.onTick expects a Consumer<String[]> according to project Register class
		Register.onTick(1, ignored -> updateCache());
	}

	/** Updates tab list cache by fetching, filtering and mapping the tab list. */
	private static void updateCache() {
		List<String> tabLines = new ArrayList<>();

		for (PlayerListEntry entry : getTabEntries()) {
			if (entry == null) continue;

			Text displayName = entry.getDisplayName();

			Text profileName = null;
			if (entry.getProfile() != null) {
				profileName = Text.literal(entry.getProfile().toString());
			}

			Text text = displayName != null ? displayName : profileName;
			if (text == null) continue;

			tabLines.add(text.getString().trim());
		}

		cachedTabLines = tabLines;
	}

	/**
	 * Returns a list of all PlayerListEntry objects from the current tab list.
	 */
	public static Collection<PlayerListEntry> getTabEntries() {
		if (mc.player == null || mc.player.networkHandler == null) return Collections.emptyList();

		return mc.player.networkHandler.getPlayerList();
	}

	/**
	 * Finds the value associated with a specific key in the tab list entries.
	 * The key should be a prefix that appears at the start of the line in the tab list.
	 */
	public static String findInfo(String key) {
		for (String line : cachedTabLines) {
			if (line.startsWith(key)) {
				return line.substring(key.length()).trim();
			}
		}

		return null;
	}

	/** Returns the cached tab. */
	public static List<String> getCachedTabLines() {
		return Collections.unmodifiableList(cachedTabLines);
	}

}
