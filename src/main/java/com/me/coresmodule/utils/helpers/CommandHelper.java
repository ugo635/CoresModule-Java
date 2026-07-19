package com.me.coresmodule.utils.helpers;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Arrays;
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

    /**
     * Creates a LiteralArgumentBuilder with the String literal
     * Ex:
     * <pre><code>
     *     literal("Exemple") // <- Will create the literal for the command /Exemple
     *     // And can be used with:
     *
     * </code></pre>
     *
     * @param literal The literal.
     * @return A LiteralArgumentBuilder for the command argument.
     */
    public static LiteralArgumentBuilder<FabricClientCommandSource> literal(String literal) {
        return ClientCommands.literal(literal);
    }

    /**
     * Creates a LiteralArgumentBuilder for a command argument that suggests a list of literals.
     * Ex:
     * <pre><code>
     *     literals("Exemple", "SEE") // <- Will create the literal for the command /Exemple SEE
     *     // And can be used with:
     *
     * </code></pre>
     *
     * @param literals The list of literals to suggest.
     * @return A LiteralArgumentBuilder for the command argument.
     */
    public static LiteralArgumentBuilder<FabricClientCommandSource> literals(String... literals) {
        if (literals.length == 0) throw new IllegalArgumentException("literals list needs at least 1 argument");

        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommands.literal(literals[0]);
        LiteralArgumentBuilder<FabricClientCommandSource> current = root;

        for (int i = 1; i < literals.length; i++) {
            LiteralArgumentBuilder<FabricClientCommandSource> next = ClientCommands.literal(literals[i]);
            current.then(next); // then() returns `this` (current), not `next`
            current = next;     // so we manually advance the pointer
        }

        return root;
    }


    /**
     * Creates a chain of nested {@link LiteralArgumentBuilder}s from the given literals,
     * attaching the given command to the final (leaf) literal in the chain.
     * <p>
     * Unlike {@link #literals(String...)}, this overload lets you specify the command to execute
     * directly, since the plain literal-only version has no way to attach {@code .executes(...)}
     * to the leaf node — calling {@code .executes(...)} on its return value attaches it to the
     * <b>root</b> node instead, which silently breaks command registration when multiple command
     * trees sharing the same root are merged by Brigadier.
     * <p>
     * Ex:
     * <pre><code>
     *     literals(context -> {
     *         Chat.chat("Hello!");
     *         return 1;
     *     }, "example", "SEE") // <- Will create and attach the command to /example SEE
     * </code></pre>
     *
     * @param command       The command to execute when the full literal chain is matched.
     * @param literalStrings The literals to chain, in order (e.g. "example", "SEE" for /example SEE).
     * @return The root {@link LiteralArgumentBuilder} of the chain, ready to be passed to
     *         {@code dispatcher.register(...)}.
     * @throws IllegalArgumentException if no literals are provided.
     */
    public static LiteralArgumentBuilder<FabricClientCommandSource> literals(
            com.mojang.brigadier.Command<FabricClientCommandSource> command,
            String... literalStrings) {
        List<String> literals = Arrays.asList(literalStrings);
        if (literals.isEmpty()) throw new IllegalArgumentException("literals list needs at least 1 argument");

        // Configure the leaf FIRST, before it's ever passed to a .then()
        LiteralArgumentBuilder<FabricClientCommandSource> current =
                ClientCommands.literal(literals.getLast());
        current.executes(command);

        // Then wrap it backward: each parent only gets `.then()`'d once its child is fully built
        for (int i = literals.size() - 2; i >= 0; i--) {
            LiteralArgumentBuilder<FabricClientCommandSource> parent = ClientCommands.literal(literals.get(i));
            parent.then(current);
            current = parent;
        }

        return current; // now the actual root, fully wired
    }
}
