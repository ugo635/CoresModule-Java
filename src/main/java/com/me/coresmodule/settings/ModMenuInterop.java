package com.me.coresmodule.settings;

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuInterop extends ModMenuApi {
    @Override
    public ConfigScreenFactory getModConfigScreenFactory() {
        return ConfigScreenFactory(parent -> {
           RessourcefulConfigScreen.getFactory("cm").apply(parent);
        });
    }
}

