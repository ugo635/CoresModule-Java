package com.me.coresmodule.features.priv;

import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.events.annotations.CmEvent;
import com.me.coresmodule.utils.events.impl.OnWorldJoin;

import static com.me.coresmodule.CoresModule.mc;

public class MainPrivate {
    public static boolean isRegistered = false;

    @CmEvent
    public static void onWorldJoin(OnWorldJoin event) {
        if (mc.player == null || isRegistered) return;
        isRegistered = true;

        if (!TextHelper.getUnFormattedString(mc.player.getName()).equals("JudgementCorePls")) return;
        playerTracker.register();
    }
}