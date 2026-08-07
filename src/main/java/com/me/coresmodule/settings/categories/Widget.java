package com.me.coresmodule.settings.categories;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

@Category("Widgets")
public class Widget {
    @ConfigEntry(id = "widgetsEnabled", translation = "Enable HUD widgets")
    public static Observable<Boolean> enabled = Observable.of(true);

    @ConfigEntry(id = "widgetBackground", translation = "Draw a background box behind widgets")
    public static Observable<Boolean> showBackground = Observable.of(true);

    @ConfigEntry(id = "widgetMinimalStyle", translation = "Use a minimal style (no border, lighter background)")
    public static Observable<Boolean> minimalStyle = Observable.of(false);

    @ConfigEntry(id = "widgetScale", translation = "Scale of rendered widgets")
    public static Observable<Float> scale = Observable.of(1.0f);
}
