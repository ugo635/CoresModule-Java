package com.me.coresmodule.utils.render.overlay;

import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.events.annotations.CmEvent;
import com.me.coresmodule.utils.events.impl.AfterHudRenderer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;

import static com.me.coresmodule.CoresModule.mc;

public final class OverlayManager {

    public static final List<Overlay> overlays = new ArrayList<>();

    private OverlayManager() {
    }

    public static void register() {
        registerRenderer();
        registerMouseLeftClick();

        Register.command("cmguis", args -> {
            mc.send(() -> mc.setScreen(new OverlayEditScreen()));
        });
    }

    public static void render(DrawContext drawContext, String renderScreen) {
        double scaleFactor = mc.getWindow().getScaleFactor();
        double mouseX = mc.mouse.getX() / scaleFactor;
        double mouseY = mc.mouse.getY() / scaleFactor;

        for (Overlay overlay : List.copyOf(overlays)) {
            if (renderScreen.isEmpty()) {
                overlay.render(drawContext, mouseX, mouseY);
            }
        }
    }

    public static void render(DrawContext drawContext) {
        render(drawContext, "");
    }

    public static void postRender(DrawContext drawContext, Screen renderScreen) {
        double scaleFactor = mc.getWindow().getScaleFactor();
        double mouseX = mc.mouse.getX() / scaleFactor;
        double mouseY = mc.mouse.getY() / scaleFactor;

        for (Overlay overlay : List.copyOf(overlays)) {
            if (overlay.allowedGuis.contains(renderScreen.getTitle().getString())) {
                overlay.render(drawContext, mouseX, mouseY);
            }
        }
    }

    public static void registerRenderer() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.afterRender(screen).register((renderScreen, drawContext, mouseX, mouseY, tickDelta) -> {
                if (!(renderScreen instanceof OverlayEditScreen)) {
                    postRender(drawContext, renderScreen);
                }
            });
        });
    }

    public static void registerMouseLeftClick() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.afterMouseClick(screen).register((clickedScreen, mouseX, mouseY, button) -> {
                if (!(clickedScreen instanceof OverlayEditScreen) && button == 0) {
                    for (Overlay overlay : overlays) {
                        overlay.overlayClicked(mouseX, mouseY);
                    }
                }
            });
        });
    }

    @CmEvent
    public static void draw(AfterHudRenderer event) {
        DrawContext context = event.drawContext;

        String title = mc.currentScreen != null
                ? mc.currentScreen.getTitle().getString()
                : "";

        render(context, title);
    }
}
