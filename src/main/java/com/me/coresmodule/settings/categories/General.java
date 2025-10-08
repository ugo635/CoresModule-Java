package com.me.coresmodule.settings.categories;


import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;


@Category("category")
public class General {
    @ConfigEntry(
            id = "demoInteger",
            translation = "1"
    )
    public static int demoInteger = 1;
}