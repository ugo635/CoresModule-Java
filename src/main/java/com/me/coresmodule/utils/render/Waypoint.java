package com.me.coresmodule.utils.render;

import com.me.coresmodule.utils.math.CmVectors;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;

import static com.me.coresmodule.CoresModule.mc;

public class Waypoint {

    public String text;
    public double x;
    public double y;
    public double z;
    public float r;
    public float g;
    public float b;
    public int ttl;
    public String type;
    public HashMap<String, Object> typeInfo;
    public boolean line;
    public boolean beam;
    public boolean distance;
    public float lineWidth;
    public float yPlus;

    public CmVectors pos;
    public Color color;
    public int hexCode;
    public double alpha = 0.5;
    public boolean hidden = false;
    public long creation = System.currentTimeMillis();
    public boolean formatted = false;
    public double distanceRaw = 0.0;
    public String distanceText = "";
    public String formattedText = "";
    public String warp = null;

    /**
     * A class representing a waypoint in the game.
     *
     * @param text      The text to display on the waypoint.
     * @param x         The x-coordinate of the waypoint.
     * @param y         The y-coordinate of the waypoint.
     * @param z         The z-coordinate of the waypoint.
     * @param r         The red color component of the waypoint.
     * @param g         The green color component of the waypoint.
     * @param b         The blue color component of the waypoint.
     * @param ttl       The time to live for the waypoint in seconds (0 for infinite).
     * @param type      The type of the waypoint for customization.
     * @param typeInfo  The information related to the type if needed.
     * @param line      Whether to draw a line to the waypoint.
     * @param beam      Whether to draw a beam at the waypoint.
     * @param distance  Whether to display the distance to the waypoint in meters (blocks).
     * @param lineWidth The width of the line.
     * @param yPlus     How much to add to the y-coordinate of the line compared to the y-coordinate of the waypoint.
     */
    public Waypoint(String text, double x, double y, double z,
                    float r, float g, float b,
                    int ttl, String type, HashMap<String, Object> typeInfo,
                    boolean line, boolean beam, boolean distance, float lineWidth, float yPlus) {

        this.text = text;
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.g = g;
        this.b = b;
        this.ttl = ttl;
        this.type = type;
        this.typeInfo = typeInfo;
        this.line = line;
        this.beam = beam;
        this.distance = distance;
        this.lineWidth = lineWidth;
        this.yPlus = yPlus;

        this.pos = new CmVectors(x, y, z);
        this.color = new Color(r, g, b);
        this.hexCode = this.color.getRGB();
    }

    /**
     * A class representing a waypoint in the game.
     *
     * @param text     The text to display on the waypoint.
     * @param x        The x-coordinate of the waypoint.
     * @param y        The y-coordinate of the waypoint.
     * @param z        The z-coordinate of the waypoint.
     * @param r        The red color component of the waypoint.
     * @param g        The green color component of the waypoint.
     * @param b        The blue color component of the waypoint.
     */
    public Waypoint(String text, double x, double y, double z,
                    float r, float g, float b, float lineWidth) {
        this(text, x, y, z, r, g, b, 0, "normal", new HashMap<String, Object>() , false, true, true, lineWidth, 0f);
    }

    public double distanceToPlayer() {
        BlockPos playerPos = mc.player.getBlockPos();
        return Math.sqrt(Math.pow(playerPos.getX() - this.x, 2) +
                        Math.pow(playerPos.getY() - this.y, 2) +
                        Math.pow(playerPos.getZ() - this.z, 2));

    }

    public void setText(String s) {
        this.text = s;
    }

    public void format(Object... formatters) {
        this.formattedText = String.format(this.text, formatters);
        this.formatted = true;
    }

    public void hide() {
        this.hidden = true;
    }

    public void show() {
        this.hidden = false;
    }

    public void render(WorldRenderContext context) {
        if (this.hidden) return;
        RenderUtil.renderWaypoint(
                context,
                this.formattedText,
                this.pos,
                new float[] {this.r, this.g, this.b},
                this.hexCode,
                (float) this.alpha,
                true,
                this.line,
                this.beam,
                this.lineWidth,
                this.yPlus
        );
    }
}