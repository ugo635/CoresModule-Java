package com.me.coresmodule.features.priv;

import com.me.coresmodule.utils.events.Register;

public class playerTracker {
    public static void register() {
        Register.command("trackPlayer", args -> {
            
        }, "playerTracker");
    }
}
