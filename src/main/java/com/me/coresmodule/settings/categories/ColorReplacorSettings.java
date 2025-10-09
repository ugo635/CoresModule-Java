package com.me.coresmodule.settings.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;

@Category("Color Replacor")
public class ColorReplacorSettings {
    @ConfigEntry(id = "temp", translation = "temp")
    public static boolean temp = true;
}