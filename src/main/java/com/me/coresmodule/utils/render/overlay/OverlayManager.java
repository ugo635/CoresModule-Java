package com.me.coresmodule.utils.render.overlay;

import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.events.annotations.CmEvent;
import com.me.coresmodule.utils.events.impl.AfterHudRenderer;
import com.me.coresmodule.utils.events.impl.GUIMouseClick;
import com.me.coresmodule.utils.events.impl.GUIRender;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

import static com.me.coresmodule.CoresModule.mc;

public final class OverlayManager {

    public static final List<Overlay> overlays = new ArrayList<>();

    public static void register() {
        Register.command("cmguis", args -> {
            mc.execute(() -> mc.setScreen(new OverlayEditScreen()));
        });
    }

    public static void render(GuiGraphicsExtractor graphics, String renderScreen) {
        double scaleFactor = mc.getWindow().getGuiScale();
        double mouseX = mc.mouseHandler.xpos() / scaleFactor;
        double mouseY = mc.mouseHandler.ypos() / scaleFactor;

        for (Overlay overlay : List.copyOf(overlays)) {
            if (renderScreen.isEmpty()) {
                overlay.render(graphics, mouseX, mouseY);
            }
        }
    }

    public static void render(GuiGraphicsExtractor graphics) {
        render(graphics, "");
    }

    public static void postRenderGUI(GuiGraphicsExtractor graphics, Screen renderScreen) {
        double scaleFactor = mc.getWindow().getGuiScale();
        double mouseX = mc.mouseHandler.xpos() / scaleFactor;
        double mouseY = mc.mouseHandler.ypos() / scaleFactor;

        for (Overlay overlay : List.copyOf(overlays)) {
            if (overlay.allowedGuis.contains(renderScreen.getTitle().getString())) {
                overlay.render(graphics, mouseX, mouseY);
            }
        }
    }

    @CmEvent
    public static void registerRenderer(GUIRender event) {
        if (!(event.screen instanceof OverlayEditScreen)) {
            postRenderGUI(event.graphics, event.screen);
        }
    }

    @CmEvent
    public static void registerMouseLeftClick(GUIMouseClick event) {
        if (!(event.screen instanceof OverlayEditScreen) && event.button == 0) {
            for (Overlay overlay : overlays) {
                overlay.overlayClicked(event.mouseX, event.mouseY);
            }
        }
    }

    @CmEvent
    public static void draw(AfterHudRenderer event) {
        GuiGraphicsExtractor graphics = event.graphics;

        String title = mc.screen != null
                ? mc.screen.getTitle().getString()
                : "";

        render(graphics, title);
    }
}
