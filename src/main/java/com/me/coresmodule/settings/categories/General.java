package com.me.coresmodule.settings.categories;


import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;


@Category("General")
public class General {
    @ConfigEntry(id = "ahMsg", translation = "Make AH message clickable to open the AH")
    public static Observable<Boolean> ahMsg = Observable.of(true);

    @ConfigEntry(id = "spookyChest", translation = "Send a message on screen when getting a Spooky Chest")
    public static Observable<Boolean> spookyChest = Observable.of(true);

    @ConfigEntry(id = "hideHoppityHunt", translation = "Hide hoppity hunt message")
    public static Observable<Boolean> hideHoppityHunt = Observable.of(true);

    @ConfigEntry(id = "pickaceAbility", translation = "Shows a message on screen when your pickaxe ability is ready")
    public static Observable<Boolean> pickaceAbility = Observable.of(true);

    @ConfigEntry(id = "coordSound", translation = "Sound when coords are sent")
    public static Observable<Boolean> coordSound = Observable.of(true);
}