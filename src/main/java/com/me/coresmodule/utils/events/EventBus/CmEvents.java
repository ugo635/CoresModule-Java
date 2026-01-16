package com.me.coresmodule.utils.events.EventBus;

import com.me.coresmodule.utils.events.impl.OnDisconnect;
import com.me.coresmodule.utils.render.CustomItem.SaveAndLoad;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class CmEvents {
    public static void register() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> {
            System.out.println("[CoresModule] Disconnecting...");
            EventBus.emit(new OnDisconnect(handler, mc));
            SaveAndLoad.save(new OnDisconnect(handler, mc));
        });
    }
}