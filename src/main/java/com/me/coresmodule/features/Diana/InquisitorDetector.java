package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.me.coresmodule.CoresModule.LOGGER;

public class InquisitorDetector {
    private static List<BiConsumer<String, ArmorStandEntity>> mobDeathListeners = new ArrayList<>();

    public static void onMobDeath(BiConsumer<String, ArmorStandEntity> listener) {
        mobDeathListeners.add(listener);
    }


    public static void register() {
        Register.command("InqAmt", ignored -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world != null) {
                for (Entity entity : world.getEntities()) {
                    String name = entity.getName().getString(); // removes formatting codes if needed
                    if (name.contains("Graveyard")) { // or equals, depending on exact name
                        Chat.chat("§aInquisitor Detected");
                        
                    }
                }

            }

        });


    }
}
