package com.me.coresmodule.features.priv;

import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

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

                                    if (name.toLowerCase().startsWith(typed)) {
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
            Helper.exactSleep(1250, () -> {
                stop = false;
            });
        });
    }

    /**
     * This one is bugged, only the player has his username as option, and can't delay to wait for it to load, so not usable.
     */
    public static void trigger() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            reg(dispatcher);
        });
    }

    public static void reg(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> tracker =
                ClientCommandManager.literal("trackPlayer")
                        .executes(context -> 1);

        for (Entity entity : getAllPlayers()) {
            Helper.print("Got 1, size is " + getAllPlayers().size());
            String playerName = TextHelper.unFormattedString(entity.getName());
            tracker.then(ClientCommandManager.literal(playerName)
                    .executes(context -> {
                        commandExec(entity);
                        return 1;
                    })
            );
        }

        // 🚨 REQUIRED!! Register the root literal
        dispatcher.register(tracker);


    }


    public static void commandExec(Entity player) {
        String playerName = TextHelper.unFormattedString(player.getName());
        Chat.chat("§aStart tracking §b%s".formatted(playerName));
        Thread thread = new Thread(() -> {
            while (!stop) {
                double x = player.getX(), y = player.getY(), z = player.getZ();
                Chat.chat("§c[CoresModule] §a%s is at x: §b%.2f§a, y: §b%.2f§a, z: §b%.2f".formatted(playerName, x, y, z));
                try {
                    Thread.sleep(1000);
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
