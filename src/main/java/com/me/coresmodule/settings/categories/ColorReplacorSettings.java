package com.me.coresmodule.settings.categories;

import com.me.coresmodule.settings.Settings;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

import static com.me.coresmodule.CoresModule.configurator;

@Category("Color Replacor")
public class ColorReplacorSettings {
    @ConfigEntry(id = "usernameColor", translation = "Color For Username, use & + color code e.g: &a for green")
    public static Observable<String> usernameColor = Observable.of("&5");
    @ConfigEntry(id = "currentRank", translation = "Current Rank (with colors), replace all § with &")
    public static Observable<String> currentRank = Observable.of("&b[MVP&5+&b]");
    @ConfigEntry(id = "wantedRank", translation = "Custom Rank")
    public static Observable<String> wantedRank = Observable.of("&b[MVP&0+&b]");

}