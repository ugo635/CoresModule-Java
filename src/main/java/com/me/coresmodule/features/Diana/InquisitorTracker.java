package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.ScreenshotUtils;
import com.me.coresmodule.utils.events.Register;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.me.coresmodule.utils.events.EventBus.EventBus;
import com.me.coresmodule.utils.chat.Chat;
import net.minecraft.entity.decoration.ArmorStandEntity;

import static com.me.coresmodule.utils.Helper.formattedString;
import static com.me.coresmodule.utils.Helper.showTitle;
import static java.lang.Integer.parseInt;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InquisitorTracker {

    private static final String MOB_NAME = "Skeleton";
    private static final Map<Integer, ArmorStandEntity> trackedEntities = new HashMap<>();
    private static final Set<Integer> defeated = new HashSet<>();
    private static final EventBus EVENT_BUS = new EventBus();

    public static void register() {
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ArmorStandEntity armorstand) {
                trackedEntities.put(entity.getId(), armorstand);
            }
        });

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof ArmorStandEntity living) {
                trackedEntities.remove(entity.getId());
                defeated.remove(entity.getId());
            }
        });

        Register.onTick(1, entity -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            Iterator<Map.Entry<Integer, ArmorStandEntity>> iterator = trackedEntities.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, ArmorStandEntity> entry = iterator.next();
                Integer id = entry.getKey();
                ArmorStandEntity armorStand = entry.getValue();

                if (!armorStand.isAlive() || armorStand.getWorld() != world) {
                    iterator.remove();
                    defeated.remove(id);
                    continue;
                }

                checkEntity(armorStand, id);


            }
        });
    }

    private static Double extractHealth(String name) {
        Pattern pattern = Pattern.compile("([0-9]+(?:\\.[0-9]+)?[MK]?)§f/");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.find()) return null;

        String value = matcher.group(1);

        if (value.endsWith("M")) {
            Double number = parseDoubleSafe(value.substring(0, value.length() - 1));
            return number != null ? number * 1_000_000 : null;
        } else if (value.endsWith("K")) {
            Double number = parseDoubleSafe(value.substring(0, value.length() - 1));
            return number != null ? number * 1_000 : null;
        } else {
            return parseDoubleSafe(value);
        }
    }

    private static Double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void checkEntity(ArmorStandEntity entity, Integer id) {
        String name = entity.getCustomName() != null
                ? formattedString(entity.getCustomName())
                : formattedString(entity.getName());

        if (!name.contains(MOB_NAME)) return;

        Double health = extractHealth(name);

        if (health != null && health <= 0 && !defeated.contains(id)) {
            defeated.add(id);
            Chat.chat("§eMob died: §b" + name);
            showTitle("§d§lChimera!", "§c130m coins", 0, 25, 35);
            ScreenshotUtils.takeScreenshot(50);
            EVENT_BUS.emit("mobDeath", entity);
        }
    }
}
