package com.me.coresmodule.features.Diana;

import com.me.coresmodule.settings.categories.Diana;
import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.ScreenshotUtils;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.events.Register;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.mc;

public class RareDropTracker {
    public static void register() throws IOException {
        FilesHandler.createFile("chimeras.txt");
        Register.onChatMessage(Pattern.compile("^§6§lRARE DROP! (.*?)$", Pattern.DOTALL), false, (message, matchResult) -> {
            String text = TextHelper.formattedString(message);
            String drop = matchResult.group(1);
            if (Diana.RareDropSs.get() && (
                drop.contains("Enchanted Book") ||
                drop.contains("Brain Food") ||
                drop.contains("Manti-core") ||
                drop.contains("Minos Relic") ||
                drop.contains("Fateful Stinger") ||
                drop.contains("Shimmering Wool"))) {

                Helper.sleep(50, () -> {
                    mc.execute(ScreenshotUtils::takeScreenshot);
                });

                try {
                    FilesHandler.appendToFile("chimeras.txt", Helper.getCurrentTime() + " " + TextHelper.removeFormatting(text));
                } catch (IOException e) {
                    Helper.printErr("[CoresModule] RareDropTracker.java:36" + e);
                }
            }
        });
    }
}
