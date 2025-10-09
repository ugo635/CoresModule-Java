package com.me.coresmodule.features.Diana;

import com.me.coresmodule.settings.categories.General;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.ScreenshotUtils;

import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.events.Register;


import java.io.IOException;

import static com.me.coresmodule.CoresModule.mc;

import com.me.coresmodule.utils.FilesHandler;

public class InquisitorTracker {
    public static void register() throws IOException {
        Register.onChatMessage(message -> {
            String text = TextHelper.formattedString(message);
            if (text.contains("§6§lRARE DROP! §fEnchanted Book (§d§lChimera I§f)") && General.ScreenshotOnChimera) { // "§6§lRARE DROP! §fEnchanted Book (§d§lChimera I§f)"
                Helper.sleep(50, () -> {
                    mc.execute(ScreenshotUtils::takeScreenshot);
                });

                try {
                    FilesHandler.writeToFile("chimeras.txt", Helper.getCurrentTime() + " " + TextHelper.removeFormatting(text));
                } catch (IOException e) {
                    System.err.println("[CoresModule] InquisitorTracker.java:27" + e);
                }

            }
        });

        FilesHandler.createFile("chimeras.txt");
    }
}
