package com.me.coresmodule.features.priv;

import com.me.coresmodule.settings.categories.Tracker;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.Waypoint;
import com.me.coresmodule.utils.render.WaypointManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;

import static com.me.coresmodule.CoresModule.mc;

public class playerTracker {
    public static boolean stop = false;
    public static void register() {

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommandManager.literal("trackPlayer")
                        .then(ClientCommandManager.argument("player", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                String typed = builder.getRemaining().toLowerCase();

                                for (AbstractClientPlayerEntity p : getAllPlayers()) {
                                    String name = TextHelper.unFormattedString(p.getName());

                                    if (name.toLowerCase().startsWith(typed) && p.isAlive()) {
                                        builder.suggest(name);
                                    }
                                }

                                return builder.buildFuture();
                            })

                            .executes(ctx -> {
                                String name = StringArgumentType.getString(ctx, "player");
                                Entity target = getEntityByName(getAllPlayers(), name);

                                if (target == null) {
                                    Chat.chat("§cPlayer not found: " + name);
                                    return 0;
                                }

                                commandExec(target);
                                return 1;
                            })
                    )
            );
        });

        Register.command("stopTracking", args -> {
            stop = true;

            Helper.sleep(100, () -> {
                // Remove waypoint from the list
                WaypointManager.waypoints.removeIf(wp -> "playerTracker".equals(wp.type));


                stop = false;
                Chat.chat("§cStopped tracking all players and removed their waypoints");
            });
        });

    }

    public static void commandExec(Entity player) {
        String playerName = TextHelper.unFormattedString(player.getName());
        Chat.chat("§aStart tracking §b%s".formatted(playerName));

        Thread thread = new Thread(() -> {
            while (!stop) {
                double x = player.getX(), y = player.getY(), z = player.getZ();

                /*
                Chat.chat("§c[CoresModule] §a%s is at x: §b%.2f§a, y: §b%.2f§a, z: §b%.2f"
                        .formatted(playerName, x, y, z));
                 */

                HashMap<String, Object> typeInfo = new HashMap<>();
                typeInfo.put("PlayerEntity", player);
                typeInfo.put("x", x);
                typeInfo.put("y", y);
                typeInfo.put("z", z);

                // Remove any existing waypoint for this player
                WaypointManager.waypoints.removeIf(wp -> wp.typeInfo.get("PlayerEntity") == player);

                // Add the new waypoint
                WaypointManager.waypoints.add(new Waypoint(
                        "Player: ", x, y, z, 1f, 1f, 1f, 0, "playerTracker", typeInfo,
                        Tracker.doWaypoint.get(), Tracker.doBeam.get(), true, Tracker.lineWidth.get(), 0
                ));

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


        });

        thread.setDaemon(true);
        thread.start();
    }


    /**
     * @return An ArrayList with all the Entities in the world
     */
    public static ArrayList<AbstractClientPlayerEntity> getAllPlayers() {
        return mc.world != null ? (ArrayList<AbstractClientPlayerEntity>) mc.world.getPlayers() : new ArrayList<net.minecraft.client.network.AbstractClientPlayerEntity>();
    }

    /**
     * @param entities An ArrayList with all the Entities you want to loop through
     * @param name The name the entity has to match to be returned
     * @return The entity that matches the name of {@code name}, if multiple occurences, it will be the first one.
     */
    public static Entity getEntityByName(ArrayList<AbstractClientPlayerEntity> entities, String name) {
        for (Entity entity : entities) {
            if (TextHelper.unFormattedString(entity.getName()).equalsIgnoreCase(name)) return entity;
        }
        return null;
    }
}
