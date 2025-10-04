package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.TakeSS;
import com.me.coresmodule.utils.events.InquisitorTracker;
import com.me.coresmodule.utils.chat.Chat;
import static com.me.coresmodule.utils.Helper.formattedString;
import static com.me.coresmodule.utils.Helper.removeFormatting;
import com.me.coresmodule.utils.events.EventBus.EventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.ArmorStandEntity;
import com.me.coresmodule.utils.events.EventBus.EntityLoadEvent;

/**
 * Tracks a specific mob by name and emits events when it spawns/loads or dies/unloads.
 */
public class inquisitorTracker {
    public static void register() {
        String trackedMob = "Skeleton";

        EventBus.on("entityLoad", data -> {
            EntityLoadEvent event = (EntityLoadEvent) data;
            String mobName = event.getEntity().getName().getString();

            if (mobName.contains(trackedMob)) {
                Chat.chat("§aMob §b%s §aspawned".formatted(trackedMob));
            }
        });

        EventBus.on("entityUnload", data -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            EntityLoadEvent event = (EntityLoadEvent) data;
            String mobName = event.getEntity().getName().getString();

            if (mobName.contains(trackedMob) && event.getEntity().distanceTo(player) <= 30) {
                Chat.chat("§aMob §b%s §adied".formatted(trackedMob));
                TakeSS.copyScreenshotToClipboard();
            }
        });
    }
}

