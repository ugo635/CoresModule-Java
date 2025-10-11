package com.me.coresmodule.settings.categories;


import com.me.coresmodule.settings.Settings;
import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

import static com.me.coresmodule.CoresModule.configurator;


@Category("General")
public class General {
    @ConfigEntry(id = "ScreenshotOnChimera", translation = "Screenshot On Chimera")
    public static Observable<Boolean> ScreenshotOnChimera = Observable.of(true);

    @ConfigEntry(id = "ahMsg", translation = "Make AH message clickable to open the AH")
    public static Observable<Boolean> ahMsg = Observable.of(true);

    @ConfigEntry(id = "spookyChest", translation = "Send a message on screen when getting a Spooky Chest")
    public static Observable<Boolean> spookyChest = Observable.of(true);

}