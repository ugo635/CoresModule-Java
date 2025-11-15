package com.me.coresmodule.utils.render;

import com.me.coresmodule.settings.categories.Tracker;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WaypointManager {

    public static List<Waypoint> waypoints = new CopyOnWriteArrayList<>(List.of(
            new Waypoint(
                    "Hellooo",
                    0.0, 100.0, 0.0,
                    1f, 1f, 1f,
                    0, "none", new HashMap<>(),
                    Tracker.doWaypoint.get(), Tracker.doBeam.get(), true,
                    Tracker.lineWidth.get(), 0
            )
    ));

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            for (Waypoint waypoint : waypoints) {
                switch (waypoint.type) {
                    case "" -> {
                        // No use yet
                        waypoint.setText("§aDistance:§b %.2f");
                        waypoint.format(waypoint.distanceToPlayer());
                        waypoint.render(context);
                    }
                    default -> {
                        waypoint.setText("§aDistance:§b %.2f");
                        waypoint.format(waypoint.distanceToPlayer());
                        waypoint.render(context);
                    }
                }
            }
        });
    }
}
