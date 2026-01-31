package com.me.coresmodule.utils.events.impl;

import com.me.coresmodule.utils.events.EventBus.EventBus;
import com.me.coresmodule.utils.render.overlay.Overlay;
import com.me.coresmodule.utils.render.overlay.OverlayEditScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

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

            ScreenEvents.afterRender(screen).register((s, drawContext, mouseX, mouseY, tickDelta) -> {
                EventBus.emit(new GUIRender(drawContext, s, mouseX, mouseY, tickDelta));
            });
        });
    }
}