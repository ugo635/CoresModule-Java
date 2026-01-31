package com.me.coresmodule.settings.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

@Category("Tracker")
public class Tracker {
    @ConfigEntry(id = "doWaypoint", translation = "Makes a waypoint appear at the player's tracked location")
    public static Observable<Boolean> doWaypoint = Observable.of(true);

    @ConfigEntry(id = "doBeam", translation = "Makes a beacon beam appear at the player's tracked location")
    public static Observable<Boolean> doBeam = Observable.of(true);

    @ConfigEntry(id = "doLine", translation = "Makes a line from your cursor to the player's tracked location")
    public static Observable<Boolean> doLine = Observable.of(true);

    @ConfigEntry(id = "lineWidth", translation = "The width of the line")
    public static Observable<Float> lineWidth = Observable.of(1.5f);
}
