package com.me.coresmodule.features.farming;

import com.me.coresmodule.settings.categories.Farming;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.SoundHandler;
import com.me.coresmodule.utils.TabList;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.events.Register;

public class PestCooldown {
    public static boolean warned5s = false;
    public static boolean warnedReady = false;

    public static void register() {
        Register.onChatMessage(message -> {
            String msg = TextHelper.getUnFormattedString(message);
            if (msg.contains("ൠ Pest have spawned in Plot -")) {
                Helper.sleep(5000, () -> {
                    warned5s = false;
                    warnedReady = false;
                });
            }
        });

        Register.onTick(20, args -> {
            if (!Helper.isInGarden() || !Farming.pestCooldown.get()) return;
            int time = getTime();

            if (time <= 5 && time > 0 && !warned5s && Helper.isInGarden()) {
                Helper.showTitle("§cPest Cooldown Ready", "§c In 5s", 0, 25, 15);
                if (Farming.soundPestCooldown.get()) SoundHandler.playCustomSound("ding");
                warned5s = true;
            }

            if (time == 0 && !warnedReady && Helper.isInGarden()) {
                Helper.showTitle("§cPest Cooldown Ready", "", 0, 25, 15);
                if (Farming.soundPestCooldown.get()) SoundHandler.playCustomSound("ding");
                warnedReady = true;
            }
        });
    }

    private static int getTime() {
        String timeString = TabList.findInfo("Cooldown: ");

        if (timeString == null || timeString.contains("MAX PESTS")) return -1;
        if (timeString.contains("m")) return 60; // Case X minutes Y seconds, we just don't care abt the value
        timeString = timeString.replace("s", "").trim();

        System.out.println("timeString: " + timeString);
        return timeString.equals("READY") ? 0 : Integer.parseInt(timeString);
    }
}
