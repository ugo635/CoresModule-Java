package com.me.coresmodule.utils.render.hud.overlay;

import com.me.coresmodule.utils.helpers.Helper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.me.coresmodule.CoresModule.mc;

public class Overlay {
    String name;
    float x;
    float y;
    float scale;
    List<String> allowedGuis;
    private List<OverlayTextLine> exampleView;
    private List<OverlayTextLine> lines = new ArrayList<>();
    private Supplier<Boolean> condition = () -> true;
    public boolean selected = false;

    public Overlay(String name, float x, float y) {
        this(name, x, y, 1.0f, List.of("Chat screen"), List.of());
    }

    public Overlay(String name, float x, float y, float scale) {
        this(name, x, y, scale, List.of("Chat screen"), List.of());
    }

    public Overlay(String name, float x, float y, float scale, List<String> allowedGuis) {
        this(name, x, y, scale, allowedGuis, List.of());
    }

    public Overlay(String name, float x, float y, float scale, List<String> allowedGuis, List<OverlayTextLine> exampleView) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.allowedGuis = allowedGuis;
        this.exampleView = exampleView;
    }

    public void register() {
        OverlayData.register();
        if (OverlayData.overlays.containsKey(name)) {
            OverlayValues data = (OverlayValues) OverlayData.overlays.get(name);
            x = data.x;
            y = data.y;
            scale = data.scale;
        } else {
            OverlayData.overlays.put(name, new OverlayValues(x, y, scale));
        }
        OverlayManager.overlays.add(this);
    }

    public void setCondition(Supplier<Boolean> condition) {
        this.condition = condition;
    }

    public boolean checkCondition() {
        return condition.get();
    }

    public void addLine(OverlayTextLine line) {
        lines.add(line);
    }

    public void addLineAt(int index, OverlayTextLine line) {
        lines.add(index, line);
    }

    public void addLines(List<OverlayTextLine> newLines) {
        lines.addAll(newLines);
    }

    public void setLines(List<OverlayTextLine> newLines) {
        lines = new ArrayList<>(newLines);
    }

    public void removeLine(OverlayTextLine line) {
        lines.remove(line);
    }

    public void removeLines(List<OverlayTextLine> toRemove) {
        lines.removeAll(toRemove);
    }

    public void clearLines() {
        lines = new ArrayList<>();
    }

    public List<OverlayTextLine> getLines() {
        if (lines.isEmpty() && !exampleView.isEmpty() && mc.screen instanceof OverlayEditScreen) {
            return exampleView;
        }
        return lines;
    }

    public void overlayClicked(double mouseX, double mouseY) {
        if (!allowedGuis.contains(Helper.getGuiName())) return;
        if (!isOverOverlay(mouseX, mouseY)) return;

        Font font = mc.font;
        float currentY = y / scale;
        float currentX = x / scale;

        for (OverlayTextLine line : getLines()) {
            if (!line.checkCondition()) continue;

            line.lineClicked(mouseX, mouseY, currentX * scale, currentY * scale, font, scale);

            if (line.linebreak) {
                currentY += font.lineHeight + 1;
                currentX = x / scale;
            } else {
                currentX += font.width(line.text) / scale;
            }
        }
    }

    public int getTotalHeight() {
        Font font = mc.font;

        int totalHeight = 0;
        for (OverlayTextLine line : getLines()) {
            if (line.linebreak && line.checkCondition()) {
                totalHeight += font.lineHeight + 1;
            }
        }
        return totalHeight;
    }

    public int getTotalWidth() {
        var font = mc.font;

        int maxWidth = 0;
        int currentWidth = 0;

        for (OverlayTextLine line : getLines()) {
            currentWidth += font.width(line.text);
            if (line.linebreak) {
                if (currentWidth > maxWidth) maxWidth = currentWidth;
                currentWidth = 0;
            }
        }

        if (currentWidth > maxWidth) maxWidth = currentWidth;
        return maxWidth;
    }

    public boolean isOverOverlay(double mouseX, double mouseY) {
        if (!condition.get()) return false;
        float totalWidth = getTotalWidth() * scale;
        float totalHeight = getTotalHeight() * scale;

        return mouseX >= x && mouseX <= x + totalWidth && mouseY >= y && mouseY <= y + totalHeight;
    }

    public void render(GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        if (!condition.get()) return;
        Font font = mc.font;

        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);

        float currentY = y / scale;
        float currentX = x / scale;

        int totalWidth = getTotalWidth();
        int totalHeight = getTotalHeight();

        if (selected) {
            drawDebugBox(graphics, (int) currentX, (int) currentY, totalWidth, totalHeight);
            graphics.text(font, "X: " + (int) x + " Y: " + (int) y + " Scale: " + String.format("%.1f", scale), (int) currentX, (int) (currentY - font.lineHeight - 1), new Color(255, 255, 255, 200).getRGB(), true);
        }

        if (isOverOverlay(mouseX, mouseY) && mc.screen instanceof OverlayEditScreen) {
            graphics.fill((int) currentX, (int) currentY, (int) (currentX + totalWidth), (int) (currentY + totalHeight), new Color(0, 0, 0, 100).getRGB());
        }

        for (OverlayTextLine line : getLines()) {
            if (!line.checkCondition()) continue;

            if (allowedGuis.contains(Helper.getGuiName())) {
                line.updateMouseInteraction(mouseX, mouseY, currentX * scale, currentY * scale, font, scale, graphics);
            }

            line.draw(graphics, (int) currentX, (int) currentY, font);

            if (line.linebreak) {
                currentY += font.lineHeight + 1;
                currentX = x / scale;
            } else {
                currentX += font.width(line.text);
            }
        }

        graphics.pose().popMatrix();
    }

    private void drawDebugBox(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.outline(x, y, width, height, new Color(255, 0, 0, 170).getRGB());
    }
}
