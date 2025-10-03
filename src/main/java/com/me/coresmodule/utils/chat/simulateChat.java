package com.me.coresmodule.utils.chat;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class simulateChat {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((((dispatcher, registryAccess) ->  {
            dispatcher.register(ClientCommandManager.literal("simulateChat")
                    .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                            .executes(context -> {
                                String message = StringArgumentType.getString(context, "message").replaceAll("&", "§");
                                Chat.chat(message);
                                Chat.command("time");
                            return 1;
                            })
                    )
            );
        })));
    }
}

