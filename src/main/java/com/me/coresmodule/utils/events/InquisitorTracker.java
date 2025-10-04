package com.me.coresmodule.utils.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.ArmorStandEntity;
import com.me.coresmodule.utils.events.EventBus.EventBus;
import com.me.coresmodule.utils.chat.Chat;
import static com.me.coresmodule.utils.Helper.formattedString;
import static com.me.coresmodule.utils.Helper.removeFormatting;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks a specific mob by name and emits events when it spawns/loads or dies/unloads.
 */
public class InquisitorTracker {

    private static final String MOB_NAME = "Minos Inquisitor";
    private static final Set<Integer> trackedEntities = new HashSet<>();
    private static final EventBus EVENT_BUS = new EventBus();

    public static void register() {
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ArmorStandEntity armorStand) {
                checkEntity(armorStand, true);
            }
        });

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof ArmorStandEntity armorStand) {
                checkEntity(armorStand, false);
            }
        });
    }


    public static void onMobSpawn(java.util.function.Consumer<ArmorStandEntity> callback) {
        EVENT_BUS.on("mobSpawn", data -> {
            if (data instanceof ArmorStandEntity armorStand) {
                callback.accept(armorStand);
            }
        });
    }

    public static void onMobDeath(java.util.function.Consumer<ArmorStandEntity> callback) {
        EVENT_BUS.on("mobDeath", data -> {
            if (data instanceof ArmorStandEntity armorStand) {
                callback.accept(armorStand);
            }
        });
    }


    private static void checkEntity(ArmorStandEntity entity, boolean isLoad) {
        String name = entity.getCustomName() != null
                ? formattedString(entity.getCustomName())
                : formattedString(entity.getName());

        if (!name.contains(MOB_NAME)) return;

        int id = System.identityHashCode(entity); // unique tracking ID

        if (isLoad && !trackedEntities.contains(id)) {
            trackedEntities.add(id);
            Chat.chat("§aMob " + name + " loaded");
            EVENT_BUS.emit("mobSpawn", entity);
        } else if (!isLoad && trackedEntities.contains(id)) {
            trackedEntities.remove(id);
            Chat.chat("§cMob " + name + " died/unloaded");
            EVENT_BUS.emit("mobDeath", entity);
        }
    }
}
