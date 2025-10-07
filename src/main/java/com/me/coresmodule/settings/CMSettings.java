package com.me.coresmodule.settings;

import com.me.coresmodule.utils.chat.Chat;
import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigButton;


@Config(value = "coresmodule")
public class CMSettings {
    public static final CMSettings INSTANCE = new CMSettings();
    private CMSettings() {
        final Runnable button = () -> {
            Chat.chat("§5Hi");
        };
    }
}
