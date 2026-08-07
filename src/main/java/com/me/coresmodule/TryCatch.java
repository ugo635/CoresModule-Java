package com.me.coresmodule;

import com.me.coresmodule.features.diana.RareDropTracker;
import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.helpers.Helper;

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

    }
}