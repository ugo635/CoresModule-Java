package com.me.coresmodule.utils.render.overlay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
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
        void run(DrawContext drawContext, TextRenderer textRenderer);
    }

    public interface Condition {
        boolean check();
    }

    public OverlayTextLine(String text) {
        this.text = text;
    }

    public void setText(String newText) {
        this.text = newText;
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

    private void hover(DrawContext drawContext, TextRenderer textRenderer) {
        if (isHovered && hoverAction != null) hoverAction.run(drawContext, textRenderer);
    }

    public void lineClicked(double mouseX, double mouseY, float x, float y, TextRenderer textRenderer, float scale) {
        if (text.isEmpty() || clickAction == null) return;
        if (isMouseOver(mouseX, mouseY, x, y, textRenderer, scale)) {
            clickAction.run();
        }
    }

    private boolean isMouseOver(double mouseX, double mouseY, float x, float y, TextRenderer textRenderer, float scale) {
        float textWidth = textRenderer.getWidth(text) * scale;
        float textHeight = (textRenderer.fontHeight + 1) * scale - 1;
        return mouseX >= x && mouseX <= x + textWidth && mouseY >= y && mouseY <= y + textHeight;
    }

    public void updateMouseInteraction(double mouseX, double mouseY, float x, float y, TextRenderer textRenderer, float scale, DrawContext drawContext) {
        if (text.isEmpty()) return;
        if (mouseEnterAction == null && mouseLeaveAction == null && hoverAction == null) return;

        boolean wasHovered = isHovered;
        boolean isNowHovered = isMouseOver(mouseX, mouseY, x, y, textRenderer, scale);
        isHovered = isNowHovered;

        if (isNowHovered && !wasHovered) {
            mouseEnter();
        } else if (!isNowHovered && wasHovered) {
            mouseLeave();
        }

        if (isNowHovered) {
            hover(drawContext, textRenderer);
        }
    }

    public void draw(DrawContext drawContext, int x, int y, TextRenderer textRenderer) {
        if (text.isEmpty()) return;

        this.x = x;
        this.y = y;
        this.width = textRenderer.getWidth(text);
        this.height = textRenderer.fontHeight;

        if (renderDebugBox) {
            drawContext.fill(x, y, x + width, y + height + 1, new Color(128, 128, 128, 130).getRGB());
        }

        drawContext.drawText(textRenderer, text, x, y, -1, shadow);
    }
}
