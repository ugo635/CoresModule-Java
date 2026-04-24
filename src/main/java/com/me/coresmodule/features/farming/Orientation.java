package com.me.coresmodule.features.farming;

import com.me.coresmodule.settings.categories.Farming;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.overlay.Overlay;
import com.me.coresmodule.utils.render.overlay.OverlayTextLine;

import java.util.List;

public class Orientation {
    public static Overlay overlay = new Overlay("Coordonate Helper:", 10f, 10f);;
    public static OverlayTextLine overlayText = new OverlayTextLine(""); // Needed cuz we register the overlay with overlayText added that isn't initialized yet.;

    public static void register() {
        overlay.register();
        overlay.setCondition(() -> Farming.activeOverlay.get());
        overlay.addLine(new OverlayTextLine("§6§lCoordonate Helper:"));
        overlay.addLine(overlayText);

        Register.onTick(1, args -> {
            double start = Farming.start.get();
            double end = Farming.end.get();
            overlayText.text = "§6%s: §b%.0f§6/§b%.0f §6(§b%.2f%%§6)".formatted(Farming.orientation.get(), end - start, end, Farming.start.get() / Farming.end.get());
        });
    }
}
