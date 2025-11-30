package com.me.coresmodule.settings;

import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;
import com.me.coresmodule.settings.categories.*;

@Config(
        value = "CoresModule",
        version = 1,
        categories = {
                General.class,
                Credits.class,
                Diana.class,
                Space2.class,
                CmPriv.class,
                Space.class,
                Tracker.class

        }

)
@ConfigInfo(
        title = "CoresModule beta 0.0.1", // Mod title
        description = "CoresModule is a QOL mod" // Mod description
)
public class Settings {
    @ConfigOption.Separator(value = "Welcome to CoresModule", description = "Made by JudgementCorePls")

    @Comment("This is a description")
    @ConfigEntry(id = "showWelcome", translation = "Show Welcome Message")
    public static Observable<Boolean> showWelcome = Observable.of(true);
}