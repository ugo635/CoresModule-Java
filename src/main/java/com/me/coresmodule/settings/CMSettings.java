package com.me.coresmodule.settings;

import com.me.coresmodule.utils.events.Register;
import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.settings.categories.General;

// Example category, you can make your own.
import com.teamresourceful.resourcefulconfig.demo.DemoCategory;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

@Config(
        value = "CMSettings",
        version = 1,
        categories = {
                General.class
        }

)
public class CMSettings {

    // Example boolean setting
    @ConfigEntry(id = "showWelcome", translation = "Show Welcome Message")
    public static Observable<Boolean> showWelcome = Observable.of(true);

    // Example string setting
    @ConfigEntry(id = "welcomeMessage", translation = "Welcome Message")
    public static String welcomeMessage = "§5Hi";

    // Example integer setting
    @ConfigEntry(id = "exampleInt", translation = "Example Integer")
    public static int exampleInt = 42;


}