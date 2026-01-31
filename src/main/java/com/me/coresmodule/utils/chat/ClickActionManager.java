package com.me.coresmodule.utils.chat;

import com.me.coresmodule.utils.events.Register;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages click actions by mapping a unique ID to a Runnable.
 * This allows us to execute arbitrary code from a chat message click.
 */
public class ClickActionManager {

    private static final Map<UUID, Runnable> actions = new ConcurrentHashMap<>();

    public static void register() {
        Register.command("__cm_run_clickable_action", args -> {
            if (args.length > 0) {
                try {
                    UUID actionId = UUID.fromString(args[0]);
                    executeAction(actionId);
                } catch (IllegalArgumentException ignored) {
                }
            }
        });
    }

    /**
     * Registers a Runnable to be executed on click.
     * @param onClick The Runnable to run when triggered.
     * @return A unique UUID identifying this action.
     */
    public static UUID registerAction(Runnable onClick) {
        UUID id = UUID.randomUUID();
        actions.put(id, onClick);
        return id;
    }

    /**
     * Executes the action corresponding to the given UUID.
     * The action is removed after execution to prevent memory leaks.
     * @param id The UUID of the action to execute.
     */
    public static void executeAction(UUID id) {
        Runnable action = actions.remove(id);
        if (action != null) {
            try {
                action.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Optional: check if an action exists for a given UUID.
     */
    public static boolean hasAction(UUID id) {
        return actions.containsKey(id);
    }
}
