package com.me.coresmodule.settings.categories;

import com.me.coresmodule.settings.Settings;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;

import static com.me.coresmodule.CoresModule.configurator;

@Category("Color Replacor")
public class ColorReplacorSettings {
    @ConfigEntry(id = "usernameColor", translation = "Color For Username, use & + color code e.g: &a for green")
    public static String usernameColor = "";
    @ConfigEntry(id = "currentRank", translation = "Current Rank (with colors), replace all § with &")
    public static String currentRank = "";
    @ConfigEntry(id = "wantedRank", translation = "Custom Rank")
    public static String wantedRank = "";
}