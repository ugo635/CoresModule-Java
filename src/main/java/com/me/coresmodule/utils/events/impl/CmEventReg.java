package com.me.coresmodule.utils.events.impl;

import com.me.coresmodule.utils.events.EventBus.EventBus;
import com.me.coresmodule.utils.helpers.TextHelper;
import com.me.coresmodule.utils.render.overlay.Overlay;
import com.me.coresmodule.utils.render.overlay.OverlayEditScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import static com.me.coresmodule.CoresModule.mc;

public class CmEventReg {
    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.afterMouseClick(screen).register((s, click, bool) -> {
                double mouseX = click.x();
                double mouseY = click.y();
                double button = click.button();
                EventBus.emit(new GUIMouseClick(mouseX, mouseY, button, screen));
                return bool;
            });

            ScreenEvents.afterExtract(screen).register((s, graphics, mouseX, mouseY, tickDelta) -> {
                EventBus.emit(new GUIRender(graphics, s, mouseX, mouseY, tickDelta));
            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> {
            EventBus.emit(new OnDisconnect(handler, mc));
        });

        ClientPlayConnectionEvents.JOIN.register((handler, packetSender, mc) -> {
            EventBus.emit(new OnWorldJoin(handler, packetSender, mc));
        });
    }
}