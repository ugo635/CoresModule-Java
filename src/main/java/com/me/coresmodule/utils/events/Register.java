package com.me.coresmodule.utils.events;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.*;
import java.util.regex.*;

import static com.me.coresmodule.utils.TextHelper.formattedString;
import static com.me.coresmodule.utils.TextHelper.removeFormatting;
import static com.me.coresmodule.utils.events.TickScheduler.ScheduledTask;
import static com.me.coresmodule.utils.events.TickScheduler.tasks;

public class Register {

    /**
     * Registers a command with the specified name and aliases.
     * The action is executed when the command is invoked, with the provided arguments.
     *
     * @param name The name of the command.
     * @param aliases Optional aliases for the command.
     * @param action The action to execute when the command is invoked. (Lambda function)
     */
    public static void command(String name, Consumer<String[]> action, String... aliases) {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            dispatcher.register(createLiteral(name, action));
            for (String alias : aliases) dispatcher.register(createLiteral(alias, action));
        }));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> createLiteral(String name, Consumer<String[]> action) {
        return ClientCommandManager.literal(name)
                .executes(context -> { action.accept(new String[0]); return 1; })
                .then(ClientCommandManager.argument("args", StringArgumentType.greedyString())
                        .executes(context -> {
                            String argsString = StringArgumentType.getString(context, "args");
                            String[] args = Arrays.stream(argsString.split(" "))
                                    .filter(s -> !s.isEmpty())
                                    .toArray(String[]::new);
                            action.accept(args);
                            return 1;
                        }));
    }

    /**
     * Registers a tick event that executes an action every specified number of ticks.
     * @param tick The number of ticks after which the action should be executed.
     * @param action The action to execute. It receives a lambda to unregister itself.
     */
    public static void onTick(int tick, Consumer<String[]> action) {
        tasks.add(new ScheduledTask(tick, done -> action.accept(new String[0])));
    }

    /**
     * Registers an event that listens for chat messages.
     * The action receives the message as a `Text` object.
     */
    public static void onChatMessage(Consumer<Text> action) {
        ClientReceiveMessageEvents.GAME.register((message, signed) -> action.accept(message));
    }

    /**
     * Registers an event that listens for chat messages that match a regex.
     * The action receives both the message and the regex match result for easy value extraction.
     *
     * @param regex The regular expression to filter messages with.
     * @param noFormating Remove the text formatting
     * @param action The action to execute. It receives the message and the `MatchResult`.
     */
    public static void onChatMessage(Pattern regex, boolean noFormating, BiConsumer<Text, MatchResult> action) {
        ClientReceiveMessageEvents.GAME.register((message, signed) -> {
            String text = formattedString(message);
            if (noFormating) text = removeFormatting(text);
            Matcher matcher = regex.matcher(text);
            if (matcher.find()) action.accept(message, matcher.toMatchResult());
        });
    }

    /**
     * Registers a cancelable chat listener.
     * The provided Function receives the incoming message and returns:
     * - true to CANCEL/HIDE the message
     * - false to allow the message
     *
     * Note: Fabric's ALLOW_GAME expects a boolean indicating whether the message is allowed.
     * We invert the handler's boolean so that handler returning true => message hidden (ALLOW_GAME returns false).
     *
     * Example usage:
     * Register.onChatMessageCancelable(message -> {
     *     return TextHelper.formattedString(message).contains("some spammy text"); // true => hide
     * });
     */
    public static void onChatMessageCancelable(Function<Text, Boolean> action) {
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, signed) -> {
            try {
                Boolean cancel = action.apply(message);
                // If handler returns true => cancel/hide message -> ALLOW_GAME must return false
                if (cancel == null) return true; // default: allow
                return !cancel;
            } catch (Exception e) {
                // On error, do not block messages
                return true;
            }
        });
    }

    /**
     * Registers a cancelable pattern-based chat listener.
     * The action receives the message and a MatchResult; return true to cancel/hide the message.
     *
     * Example:
     * Register.onChatMessageCancelable(Pattern.compile("foo (\\d+)"), true, (message, matcher) -> {
     *     // match and decide; return true to hide message
     *     return true;
     * });
     */
    public static void onChatMessageCancelable(Pattern regex, boolean noFormating, BiFunction<Text, MatchResult, Boolean> action) {
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, signed) -> {
            String text = formattedString(message);
            if (noFormating) text = removeFormatting(text);
            Matcher matcher = regex.matcher(text);
            if (matcher.find()) {
                try {
                    Boolean cancel = action.apply(message, matcher.toMatchResult());
                    if (cancel == null) return true;
                    return !cancel;
                } catch (Exception e) {
                    return true;
                }
            }
            return true;
        });
    }

    public static void onGuiClose(Consumer<Screen> action) {
        ScreenEvents.AFTER_INIT.register((ignored, screen, ignored1, ignored2) ->
                ScreenEvents.remove(screen).register(ignored3 -> action.accept(screen)));
    }

    public static void onEntityLoad(ClientEntityEvents.Load action) {
        ClientEntityEvents.ENTITY_LOAD.register(action);
    }

    /**
     * Registers an event that listens for entities being unloaded (removed) from the client world.
     * The action receives both the entity and the client world.
     *
     * @param action The action to execute when an entity is unloaded. It receives:
     *               - {@code entity}: The entity that was removed.
     *               - {@code world}: The client world the entity belonged to.
     * Example usage:
     * <pre>
     * onEntityUnLoad((entity, world) -> {
     *     if (entity.getName().getString().contains("Minos Inquisitor")) {
     *         Helper.print("Inquisitor left the world: " + entity);
     *     }
     * });
     * </pre>
     */
    public static void onEntityUnLoad(ClientEntityEvents.Unload action) {
        ClientEntityEvents.ENTITY_UNLOAD.register(action);
    }

}