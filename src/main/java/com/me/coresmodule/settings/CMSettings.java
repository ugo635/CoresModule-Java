package com.me.coresmodule.settings;

import com.me.coresmodule.utils.events.Register;
import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;
import com.me.coresmodule.utils.chat.Chat;

// Example category, you can make your own.
import com.teamresourceful.resourcefulconfig.demo.DemoCategory;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

@Config(
        value = "CMSettings",
        version = 1,
        categories = {
                DemoCategory.class // You can use your own category here.
        }
)
public class CMSettings {

    public static void register() {
        // Register the "/cm" command to open the config screen
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("cm").executes(context -> {
                        var factory = ResourcefulConfigScreen.getFactory("CMSettings");
                        if (factory == null) {
                            Chat.chat("§cFactory de config introuvable !");
                            return 1;
                        }
                        MinecraftClient.getInstance().setScreen(factory.apply(MinecraftClient.getInstance().currentScreen));
                        Chat.chat("§aOuverture de l'écran de config...");
                        return 1;
                    })
            );
        });
    }

    // Example boolean setting
    @ConfigEntry(id = "showWelcome", translation = "Show Welcome Message")
    public static Observable<Boolean> showWelcome = Observable.of(true);

    // Example string setting
    @ConfigEntry(id = "welcomeMessage", translation = "Welcome Message")
    public static String welcomeMessage = "§5Hi";

    // Example integer setting
    @ConfigEntry(id = "exampleInt", translation = "Example Integer")
    public static int exampleInt = 42;

    static {
        showWelcome.addListener((oldValue, newValue) -> {
            if (newValue) {
                Chat.chat(welcomeMessage);
            }
        });
    }
}