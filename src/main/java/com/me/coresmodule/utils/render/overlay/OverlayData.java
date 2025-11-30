package com.me.coresmodule.utils.render.overlay;

import com.me.coresmodule.utils.FilesHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OverlayData {
    public static Map<String, OverlayValues> overlays = new HashMap<>();

    public static void register() {
        try {
            FilesHandler.createFile("overlays.json");
            String content = FilesHandler.getContent("overlays.json").trim();
            if (!content.isEmpty() && !content.equals("{}")) {
                JSONObject json = new JSONObject(content);
                Map<String, Object> temp = json.toMap();
                for (String key : temp.keySet()) {
                    Map<String, Object> values = (Map<String, Object>) temp.get(key);
                    float x = ((Number) values.get("x")).floatValue();
                    float y = ((Number) values.get("y")).floatValue();
                    float scale = ((Number) values.get("scale")).floatValue();
                    overlays.put(key, new OverlayValues(x, y, scale));
                }
            } else {
                FilesHandler.writeToFile("overlays.json", "{}");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            try {
                JSONObject json = new JSONObject(overlays);
                FilesHandler.writeToFile("overlays.json", json.toString(4));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            try {
                JSONObject json = new JSONObject(overlays);
                FilesHandler.writeToFile("overlays.json", json.toString(4));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
