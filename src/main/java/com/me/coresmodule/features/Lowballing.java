package com.me.coresmodule.features;

import com.me.coresmodule.utils.SoundHandler;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.helpers.Helper;
import com.me.coresmodule.utils.helpers.TextHelper;

public class Lowballing {
    public static void register() {
        Register.onChatMessage(msg -> {
            if (!TextHelper.containsFormattedStringOf(msg, "§b[SkyBlock]", "§eis visiting §aYour Island§e!")) return;
            Helper.showTitle("§6§lVisit", "", 5, 20, 5);
            SoundHandler.playCustomSound("ding");
        });

        // Command to declare putting on sale, and auto see claiming rewards

    }


}
