package com.me.coresmodule.features.priv;

import static com.me.coresmodule.CoresModule.mc;

public class MainPrivate {
    public static void register() {
        if (!mc.getSession().getUsername().equals("JudgementCorePls")) return;
        playerTracker.register();
    }
}