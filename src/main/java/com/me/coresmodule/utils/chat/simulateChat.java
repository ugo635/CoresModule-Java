package com.me.coresmodule.utils.chat;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import com.me.coresmodule.utils.events.Register;

import static com.me.coresmodule.CoresModule.LOGGER;

public class simulateChat {
    public static void register() {

        Register.command("simulateChat", args -> {
            if (args.length == 0) return;

            String message = String.join(" ", args).replace("&", "§");
            Chat.chat(message);
        });

    }
}

