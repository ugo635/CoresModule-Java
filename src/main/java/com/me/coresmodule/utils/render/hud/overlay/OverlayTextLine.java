package com.me.coresmodule.utils.render.hud.overlay;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.Color;

public class OverlayTextLine {
    public String text;
    public boolean shadow = true;
    public boolean linebreak = true;
    public Runnable mouseEnterAction = null;
    public Runnable mouseLeaveAction = null;
    public HoverAction hoverAction = null;
    public Runnable clickAction = null;
    public boolean isHovered = false;
    public int x = 0;
    public int y = 0;
    private int width = 0;
    private int height = 0;
    public boolean renderDebugBox = false;
    private Condition condition = () -> true;

    public interface HoverAction {
        void run(GuiGraphicsExtractor drawContext, Font font);
    }

    public interface Condition {
        boolean check();
    }

    public OverlayTextLine(String text) {
        this.text = text;
    }

    public void setText(String newComponent) {
        this.text = newComponent;
    }

    public OverlayTextLine(String text, boolean shadow, boolean linebreak) {
        this.text = text;
        this.shadow = shadow;
        this.linebreak = linebreak;
    }

    public OverlayTextLine setCondition(Condition condition) {
        this.condition = condition;
        return this;
    }

    public boolean checkCondition() {
        return condition.check();
    }

    public OverlayTextLine onMouseEnter(Runnable action) {
        this.mouseEnterAction = action;
        return this;
    }

    public OverlayTextLine onMouseLeave(Runnable action) {
        this.mouseLeaveAction = action;
        return this;
    }

    public OverlayTextLine onHover(HoverAction action) {
        this.hoverAction = action;
        return this;
    }

    public OverlayTextLine onClick(Runnable action) {
        this.clickAction = action;
        return this;
    }

    private void mouseEnter() {
        if (mouseEnterAction != null) mouseEnterAction.run();
    }

    private void mouseLeave() {
        if (mouseLeaveAction != null) mouseLeaveAction.run();
    }

    private void hover(GuiGraphicsExtractor graphics, Font font) {
        if (isHovered && hoverAction != null) hoverAction.run(graphics, font);
    }

    public void lineClicked(double mouseX, double mouseY, float x, float y, Font font, float scale) {
        if (text.isEmpty() || clickAction == null) return;
        if (isMouseOver(mouseX, mouseY, x, y, font, scale)) {
            clickAction.run();
        }
    }

    private boolean isMouseOver(double mouseX, double mouseY, float x, float y, Font font, float scale) {
        float textWidth = font.width(text) * scale;
        float textHeight = (font.lineHeight + 1) * scale - 1;
        return mouseX >= x && mouseX <= x + textWidth && mouseY >= y && mouseY <= y + textHeight;
    }

    public void updateMouseInteraction(double mouseX, double mouseY, float x, float y, Font font, float scale, GuiGraphicsExtractor graphics) {
        if (text.isEmpty()) return;
        if (mouseEnterAction == null && mouseLeaveAction == null && hoverAction == null) return;

        boolean wasHovered = isHovered;
        boolean isNowHovered = isMouseOver(mouseX, mouseY, x, y, font, scale);
        isHovered = isNowHovered;

        if (isNowHovered && !wasHovered) {
            mouseEnter();
        } else if (!isNowHovered && wasHovered) {
            mouseLeave();
        }

        if (isNowHovered) {
            hover(graphics, font);
        }
    }

    public void draw(GuiGraphicsExtractor graphics, int x, int y, Font font) {
        if (text.isEmpty()) return;

        this.x = x;
        this.y = y;
        this.width = font.width(text);
        this.height = font.lineHeight;

        if (renderDebugBox) {
            graphics.fill(x, y, x + width, y + height + 1, new Color(128, 128, 128, 130).getRGB());
        }

        graphics.text(font, text, x, y, -1, shadow);
    }
}
