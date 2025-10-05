package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.ScreenshotUtils;

import com.me.coresmodule.utils.events.Register;

import static com.me.coresmodule.CoresModule.mc;

public class InquisitorTracker {
    public static void register() {
        Register.onChatMessage(message -> {
            String text = Helper.formattedString(message);
            if (text.contains("§6§lRARE DROP! §fEnchanted Book (§d§lChimera I§f)")) {
                Helper.sleep(50, () -> mc.execute(ScreenshotUtils::takeScreenshot));
            }
        });
    }
}
