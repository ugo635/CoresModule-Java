package com.me.coresmodule.settings;

import com.me.coresmodule.settings.categories.ColorReplacorSettings;
import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.settings.categories.General;

import static com.me.coresmodule.CoresModule.configurator;

@Config(
        value = "CoresModule",
        version = 1,
        categories = {
                General.class,
                ColorReplacorSettings.class
        }

)
public class Settings {

    // Example boolean setting
    @ConfigEntry(id = "showWelcome", translation = "Show Welcome Message")
    public static Observable<Boolean> showWelcome = Observable.of(true);

}