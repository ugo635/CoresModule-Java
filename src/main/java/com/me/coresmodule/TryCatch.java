package com.me.coresmodule;

import com.me.coresmodule.features.Diana.RareDropTracker;
import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.render.CustomItem.SaveAndLoad;

import java.io.IOException;

public class TryCatch {
    public static void register() {
        try {
            FilesHandler.register();
        } catch (IOException e) {
            Helper.printErr("[CoresModule] TryCatch.java:15 " + e);
        }

        try {
            RareDropTracker.register();
        } catch (IOException e) {
            Helper.printErr("[CoresModule] TryCatch.java:21 " + e);
        }

        try {
            SaveAndLoad.register();
        } catch (IOException e) {
            Helper.printErr("[CoresModule] TryCatch.java:27 " + e);
        }
    }
}