package com.me.coresmodule.features.priv;

import com.me.coresmodule.utils.helpers.Helper;
import com.me.coresmodule.utils.helpers.TextHelper;
import com.me.coresmodule.utils.events.annotations.CmEvent;
import com.me.coresmodule.utils.events.impl.OnWorldJoin;

import static com.me.coresmodule.CoresModule.mc;

public class MainPrivate {

    @CmEvent
    public static void onWorldJoin(OnWorldJoin event) {
        Helper.sleep(200, () -> {
            if (mc.player == null) return;

            if (!TextHelper.getUnFormattedString(event.player.getName()).equals("JudgementCorePls")) return;
            playerTracker.register();
        });

    }
}