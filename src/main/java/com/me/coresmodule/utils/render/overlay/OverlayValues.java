package com.me.coresmodule.utils.render.overlay;

import java.util.Map;

public class OverlayValues {
    public float x = 0f;
    public float y = 0f;
    public float scale = 1f;

    public OverlayValues(float x, float y, float scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public Map<String, Float> toMap() {
        return Map.of(
            "x", x,
            "y", y,
            "scale", scale
        );
    }
}
