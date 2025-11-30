package com.me.coresmodule.settings.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

@Category("Credits")
public class Credits {
    @ConfigOption.Separator(
            value = "Special Thanks to",
            description = """
                    • D4rkSwift (sbo) for helping me getting started with modding and fabric
                    """
    )

    @ConfigOption.Hidden
    @ConfigEntry(id = "ignored", translation = "Hi")
    public static Observable<Boolean> ignored = Observable.of(true);
}
