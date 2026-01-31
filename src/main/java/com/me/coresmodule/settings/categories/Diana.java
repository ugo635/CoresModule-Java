package com.me.coresmodule.settings.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

@Category("Diana")
public class Diana {
    @ConfigEntry(id = "minotaurOnScreen", translation = "Message on screen when Minotaur spawns")
    public static Observable<Boolean> minotaurOnScreen = Observable.of(true);

    @ConfigEntry(id = "announceBraided", translation = "Announce Braided Griffin Feather in chat")
    public static Observable<Boolean> announceBraided = Observable.of(true);

    @ConfigEntry(id = "wrongPet", translation = "Message on screen if wrong pet eqquipped")
    public static Observable<Boolean> wrongPet = Observable.of(true);

    @ConfigEntry(id = "RareDropSs", translation = "Take a screenshot when getting a rare drop (e.g: Chimera, Wool, Core, etc...)")
    public static Observable<Boolean> RareDropSs = Observable.of(true);

    @ConfigEntry(id = "MfOverlay", translation = "Shows an overlay of your magic find")
    public static Observable<Boolean> MfOverlay = Observable.of(true);

    @ConfigEntry(id = "SphinxQuestion", translation = "Always Right Answer")
    public static Observable<Boolean> SphinxQuestion = Observable.of(true);

    @ConfigEntry(id = "ffTimer", translation = "Overlay for ff timer")
    public static Observable<Boolean> ffTimer = Observable.of(true);
}
