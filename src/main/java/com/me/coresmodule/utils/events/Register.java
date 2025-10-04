package com.me.coresmodule.utils.events;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Arrays;
import java.util.function.Consumer;

public class Register {
    public static void command(String name, Consumer<String[]> action, String... aliases) {

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            dispatcher.register(createLiteral(name, action));
            for (String alias : aliases) {
                dispatcher.register(createLiteral(alias, action));
            }
        }));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> createLiteral(String name, Consumer<String[]> action) {
        return ClientCommandManager.literal(name)
                .executes(context -> {
                    action.accept(new String[0]);
                    return 1;
                })
                .then(ClientCommandManager.argument("args", StringArgumentType.greedyString())
                        .executes(context -> {
                            String argsString = StringArgumentType.getString(context, "args");
                            String[] args = Arrays.stream(argsString.split(" "))
                                    .filter(s -> !s.isEmpty())
                                    .toArray(String[]::new);
                            action.accept(args);
                            return 1;
                        })
                );
    }

    // TODO: Do others registers

}
