package com.me.coresmodule.utils.render.hud.widget;

import com.me.coresmodule.utils.events.annotations.CmEvent;
import com.me.coresmodule.utils.events.impl.AfterHudRenderer;
import com.me.coresmodule.utils.helpers.Helper;
import com.me.coresmodule.utils.helpers.WidgetHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static com.me.coresmodule.CoresModule.mc;

/**
 * Bordered, titled HUD box that auto-sizes to fit whatever text lines are added to it.
 */
public abstract class Widget {

    private static final List<Widget> WIDGETS = new ArrayList<>();

    private final String internalID;
    private final String title;
    private final Color color;
    private final float scale;

    private final List<WidgetLine> lines = new ArrayList<>();
    private String lastError = null;

    protected int x = 0;
    protected int y = 0;
    private int width = 0;
    private int height = 0;

    private boolean visible = false;
    private Supplier<Boolean> condition = () -> true;

    public Widget(String id, String title, @Nullable Color colorValue, float scaleValue) {
        this.internalID = id;
        this.title = title;
        this.color = (colorValue == null ? Color.BLACK : colorValue);
        this.scale = Math.max(0.1f, scaleValue);
    }

    public final int getX() {
        return x;
    }

    public final void setX(int x) {
        this.x = x;
    }

    public final int getY() {
        return y;
    }

    public final void setY(int y) {
        this.y = y;
    }

    /** Scaled pixel width. */
    public final int getWidth() {
        return width;
    }

    /** Scaled pixel height. */
    public final int getHeight() {
        return height;
    }

    public final float getScale() {
        return scale;
    }

    /** Base-space width (unscaled). */
    protected final int getBaseWidth() {
        return Math.max(1, Math.round(width / scale));
    }

    /** Base-space height (unscaled). */
    protected final int getBaseHeight() {
        return Math.max(1, Math.round(height / scale));
    }

    protected final int scaled(int value) {
        return Math.max(1, Math.round(value * scale));
    }

    public final void setCondition(Supplier<Boolean> condition) {
        this.condition = condition;
    }

    protected final boolean checkCondition() {
        return condition.get();
    }

    public final boolean isVisible() {
        return visible;
    }

    public final String getInternalID() {
        return internalID;
    }

    public final void register() {
        if (!WIDGETS.contains(this)) WIDGETS.add(this);
    }

    public final void unregister() {
        WIDGETS.remove(this);
    }

    public final void addLine(String text) {
        addLine(text, -1);
    }

    public final void addLine(String text, int textColor) {
        lines.add(new WidgetLine(text, textColor));
    }

    public final void clearLines() {
        lines.clear();
    }

    public final boolean isEmpty() {
        return lines.isEmpty();
    }

    public abstract void updateContent();

    public final void update() {
        lines.clear();
        try {
            updateContent();
        } catch (Exception e) {
            if (e.getMessage() == null || !e.getMessage().equals(lastError)) {
                lastError = e.getMessage();
                Helper.printErr("Failed to update widget " + internalID + ": " + e);
            }
            lines.clear();
            lines.add(new WidgetLine("§cAn error occurred! Please check logs.", -1));
        }
        pack();
    }

    private void pack() {
        Font font = mc.font;
        List<String> texts = lines.stream().map(l -> l.text).toList();
        int[] size = WidgetHelper.pack(font, title, texts);
        width = Math.max(1, Math.round(size[0] * scale));
        height = Math.max(1, Math.round(size[1] * scale));
    }

    public void render(GuiGraphicsExtractor graphics) {
        if (!condition.get()) {
            visible = false;
            return;
        }
        visible = true;

        Font font = mc.font;
        boolean minimal = com.me.coresmodule.settings.categories.Widget.minimalStyle.get();

        // draw in base coordinates, scale pose so text and geometry both scale
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);

        int bx = Math.round(x / scale);
        int by = Math.round(y / scale);
        int bw = getBaseWidth();
        int bh = getBaseHeight();

        if (com.me.coresmodule.settings.categories.Widget.showBackground.get()) {
            WidgetHelper.drawBackground(graphics, bx, by, bw, bh, minimal);
        }

        graphics.text(font, title, bx + 8, by + 2, color.getRGB(), false);

        if (!minimal) {
            WidgetHelper.drawBorder(graphics, bx, by, bw, bh, font, title, color.getRGB());
        }

        int yOffset = by + WidgetHelper.BORDER_N;
        for (WidgetLine line : lines) {
            graphics.text(font, line.text, bx + WidgetHelper.BORDER_W, yOffset, line.color, true);
            yOffset += font.lineHeight + 1;
        }

        graphics.pose().popMatrix();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Widget widget)) return false;
        return Objects.equals(internalID, widget.internalID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalID);
    }

    private record WidgetLine(String text, int color) {
    }

    @CmEvent
    public static void onRender(AfterHudRenderer event) {
        if (!com.me.coresmodule.settings.categories.Widget.enabled.get()) return;

        for (Widget widget : List.copyOf(WIDGETS)) {
            widget.update();
            if (!widget.isEmpty()) widget.render(event.graphics);
        }
    }
}