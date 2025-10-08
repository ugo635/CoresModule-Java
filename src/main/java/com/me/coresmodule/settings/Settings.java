package com.me.coresmodule.settings;

import com.me.coresmodule.CoresModule;
import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.settings.categories.General;

import static com.teamresourceful.resourcefulconfig.demo.Demo.configurator;

// Example category, you can make your own.


@Config(
        value = "Settings",
        version = 1,
        categories = {
                General.class
        }

)
public class Settings {

    // Example boolean setting
    @ConfigEntry(id = "showWelcome", translation = "Show Welcome Message")
    public static Observable<Boolean> showWelcome = Observable.of(true);

    static {
        showWelcome.addListener((oldValue, newValue) -> {
            Chat.chat("Old Value: " + oldValue + " New Value: " + newValue);
        });
    }

    // Example string setting
    @ConfigEntry(id = "welcomeMessage", translation = "Welcome Message")
    public static String welcomeMessage = "§5Hi";

    // Example integer setting
    @ConfigEntry(id = "exampleInt", translation = "Example Integer")
    public static int exampleInt = 42;


    public static void save() {
        configurator.saveConfig(Settings.class);
    }
}