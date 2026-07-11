package com.me.coresmodule.features.farming;

import com.me.coresmodule.settings.categories.Farming;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.overlay.Overlay;
import com.me.coresmodule.utils.render.overlay.OverlayTextLine;

import static com.me.coresmodule.CoresModule.mc;

public class Orientation {
    public static Overlay overlay = new Overlay("Coordonate Helper:", 10f, 10f);;
    public static OverlayTextLine overlayComponent = new OverlayTextLine(""); // Needed cuz we register the overlay with overlayComponent added that isn't initialized yet.

    public static void register() {
        overlay.register();
        overlay.setCondition(() -> Farming.activeOverlay.get() && (!Farming.gardenOnly.get() || Helper.isInGarden()));
        overlay.addLine(new OverlayTextLine("§6§lCoordonate Helper:"));
        overlay.addLine(overlayComponent);

        Register.onTick(1, args -> {
            if (mc.player == null) return;
            double start = Farming.start.get();
            double end = Farming.end.get();
            double length = end - start;
            double currentXorZ = Farming.orientation.get() == Farming.Orientation.X ? mc.player.getX() : mc.player.getZ();
            double XorZ = currentXorZ - start;
            overlayComponent.text = "§6%s: §b%.0f§6/§b%.0f §6(§b%.2f%%§6)".formatted(Farming.orientation.get(), XorZ, length, XorZ / length * 100);
        });
    }

}
