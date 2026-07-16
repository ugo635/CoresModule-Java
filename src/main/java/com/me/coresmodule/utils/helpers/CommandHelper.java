package com.me.coresmodule.utils.helpers;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.List;

public class CommandHelper {
    public static RequiredArgumentBuilder<FabricClientCommandSource, String> argWithOptions(String argName, List<String> possibilities) {
        return ClientCommands.argument(argName, StringArgumentType.word())
                .suggests((context, builder) -> {
                    possibilities.stream()
                            .filter(s -> s.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                            .forEach(builder::suggest);
                    return builder.buildFuture();
                });
    }
}
