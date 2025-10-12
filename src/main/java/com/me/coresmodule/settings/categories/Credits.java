package com.me.coresmodule.settings.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;
import com.me.coresmodule.settings.categories.*;

@Category("Credits")
public class Credits {
    @ConfigOption.Separator(
            value = "Special Thanks to",
            description = """
                    • Resourceful Config by Team Resourceful and especially ThatGravyBoat for bearing me: Made this config API
                    • D4rkSwift for helping me getting started with modding and fabric
                    """
    )

    @ConfigEntry(id = "ignored", translation = "Hi")
    public static Observable<Boolean> ignored = Observable.of(true);
}
