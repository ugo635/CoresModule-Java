package com.me.coresmodule.settings.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

@Category("Farming")
public class Farming {
    @ConfigEntry(id = "Coordonate overlay", translation = "Turn on the coordonate helper overlay")
    public static Observable<Boolean> activeOverlay = Observable.of(false);

    @ConfigEntry(id = "Garden Only", translation = "If off, the overlay will appear outside of the garden")
    public static Observable<Boolean> gardenOnly = Observable.of(true);

    @ConfigEntry(id = "X or Z", translation = "Choose which coordonate moves when farming")
    public static Observable<Orientation> orientation = Observable.of(Orientation.X);

    @ConfigEntry(id = "Start", translation = "The X/Z coordonate of the start of the field")
    public static Observable<Double> start = Observable.of((double) 0);

    @ConfigEntry(id = "End", translation = "The X/Z coordonate of the end of the field")
    public static Observable<Double> end = Observable.of((double) 0);

    public enum Orientation {
        X,
        Z;

        @Override
        public String toString() {
            return this == X ? "X" : "Z";
        }
    }
}