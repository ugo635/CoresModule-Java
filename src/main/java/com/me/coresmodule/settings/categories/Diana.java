package com.me.coresmodule.settings.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

@Category("Diana")
public class Diana {
    @ConfigEntry(id = "minotaurOnScreen", translation = "Message on screen when Minotaur spawns")
    public static Observable<Boolean> minotaurOnScreen = Observable.of(true);

    @ConfigEntry(id = "announceMythosFrag", translation = "Announce Mythos Fragments in chat")
    public static Observable<Boolean> announceMythosFrag = Observable.of(true);
}
