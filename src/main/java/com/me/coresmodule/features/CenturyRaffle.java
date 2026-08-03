package com.me.coresmodule.features;

import com.me.coresmodule.settings.categories.General;
import com.me.coresmodule.utils.helpers.AreaHelper;
import com.me.coresmodule.utils.helpers.Helper;
import com.me.coresmodule.utils.helpers.TextHelper;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.overlay.Overlay;
import com.me.coresmodule.utils.render.overlay.OverlayTextLine;

import static com.me.coresmodule.CoresModule.mc;

public class CenturyRaffle {
    public static int eatenSlices = 0;
    public static Overlay overlay = new Overlay("Cake Eaten", 10f, 10f);
    public static OverlayTextLine textLine = new OverlayTextLine("§d§lCake Eaten: §b0");

    public static void register() {
        overlay.setCondition(() -> mc.player != null && AreaHelper.isInHub() && General.raffleSlices.get());
        overlay.addLine(textLine);
        overlay.register();

        Register.onChatMessage(msg -> {
            if (!TextHelper.containsFormattedStringOf(msg, "§lYUM!", "§eYou've eaten as much cake as you can for §9now§e!")) return;
            Helper.sleep(500, () -> {
                Helper.showTitle("§d§lMax Cake Eaten!", "", 0, 25, 10);
                eatenSlices = 0;

                textLine.setText("§d§lCake Eaten: §b" + eatenSlices);
            });

        });

        Register.onChatMessage(msg -> {
            if (!TextHelper.containsFormattedStringOf(msg, "§lCENTURY!", "§ecake slice with")) return;
            eatenSlices++;
            textLine.setText("§d§lCake Eaten: §b" + eatenSlices);
        });


    }
}
