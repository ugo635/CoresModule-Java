package com.me.coresmodule.utils.render;

import com.me.coresmodule.settings.categories.Tracker;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WaypointManager {

    public static List<Waypoint> waypoints = new CopyOnWriteArrayList<>(/*List.of(
            new Waypoint(
                    "Hellooo",
                    0.0, 100.0, 0.0,
                    1f, 1f, 1f,
                    0, "none", new HashMap<>(),
                    Tracker.doLine.get(), Tracker.doBeam.get(), true,
                    5f, 0
            )
    )*/);

    public static void register() {
        LevelRenderEvents.BEFORE_TRANSLUCENT_TERRAIN.register(context -> {
            for (Waypoint waypoint : waypoints) {
                waypoint.setComponent("§aDistance:§b %.2f");
                waypoint.format(waypoint.distanceToPlayer());
                waypoint.render(context); // box, line, text
            }
        });

        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            for (Waypoint waypoint : waypoints) {
                if (waypoint.hidden || !waypoint.beam) continue;
                RenderUtil.renderBeaconBeam(
                        context,
                        waypoint.pos,
                        1,
                        new float[] { waypoint.r, waypoint.g, waypoint.b }
                );
            }
        });
    }
}
