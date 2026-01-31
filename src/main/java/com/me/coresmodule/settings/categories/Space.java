package com.me.coresmodule.settings.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

@Category("━━━━━━━━━━━")
public class Space {
    @ConfigOption.Separator(
            value = "",
            description = ""
    )

    @ConfigOption.Hidden
    @ConfigEntry(id = "ignored", translation = "Hi")
    public static Observable<Boolean> ignored = Observable.of(true);
}
