package com.me.coresmodule.features.priv;

import static com.me.coresmodule.CoresModule.mc;

public class MainPrivate {
    public static void register() {
        System.out.println("Registering private features...");
        if (mc.player == null) {
            System.out.println("Player is null");
            return;
        }
        System.out.println("Player UUID: " + mc.player.getStringUUID());
        if (mc.player == null || !mc.player.getStringUUID().equals("eec82c33-ea9d-4628-b2e1-bf1a6b770095")) return;
        playerTracker.register();
    }
}