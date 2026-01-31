package com.me.coresmodule.utils.render.overlay;

import com.me.coresmodule.utils.Helper;
import net.minecraft.client.gui.DrawContext;

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

    public Overlay setCondition(Supplier<Boolean> condition) {
        this.condition = condition;
        return this;
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
        if (lines.isEmpty() && !exampleView.isEmpty() && mc.currentScreen instanceof OverlayEditScreen) {
            return exampleView;
        }
        return lines;
    }

    public void overlayClicked(double mouseX, double mouseY) {
        if (!allowedGuis.contains(Helper.getGuiName())) return;
        var textRenderer = mc.textRenderer;
        if (textRenderer == null) return;
        if (!isOverOverlay(mouseX, mouseY)) return;

        float currentY = y / scale;
        float currentX = x / scale;

        for (OverlayTextLine line : getLines()) {
            if (!line.checkCondition()) continue;

            line.lineClicked(mouseX, mouseY, currentX * scale, currentY * scale, textRenderer, scale);

            if (line.linebreak) {
                currentY += textRenderer.fontHeight + 1;
                currentX = x / scale;
            } else {
                currentX += textRenderer.getWidth(line.text) / scale;
            }
        }
    }

    public int getTotalHeight() {
        var textRenderer = mc.textRenderer;
        if (textRenderer == null) return 0;

        int totalHeight = 0;
        for (OverlayTextLine line : getLines()) {
            if (line.linebreak && line.checkCondition()) {
                totalHeight += textRenderer.fontHeight + 1;
            }
        }
        return totalHeight;
    }

    public int getTotalWidth() {
        var textRenderer = mc.textRenderer;
        if (textRenderer == null) return 0;

        int maxWidth = 0;
        int currentWidth = 0;

        for (OverlayTextLine line : getLines()) {
            currentWidth += textRenderer.getWidth(line.text);
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

    public void render(DrawContext drawContext, double mouseX, double mouseY) {
        if (!condition.get()) return;
        var textRenderer = mc.textRenderer;
        if (textRenderer == null) return;

        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(scale, scale, 1.0f);

        float currentY = y / scale;
        float currentX = x / scale;

        int totalWidth = getTotalWidth();
        int totalHeight = getTotalHeight();

        if (selected) {
            drawDebugBox(drawContext, (int) currentX, (int) currentY, totalWidth, totalHeight);
            drawContext.drawText(textRenderer, "X: " + (int) x + " Y: " + (int) y + " Scale: " + String.format("%.1f", scale), (int) currentX, (int) (currentY - textRenderer.fontHeight - 1), new Color(255, 255, 255, 200).getRGB(), true);
        }

        if (isOverOverlay(mouseX, mouseY) && mc.currentScreen instanceof OverlayEditScreen) {
            drawContext.fill((int) currentX, (int) currentY, (int) (currentX + totalWidth), (int) (currentY + totalHeight), new Color(0, 0, 0, 100).getRGB());
        }

        for (OverlayTextLine line : getLines()) {
            if (!line.checkCondition()) continue;

            if (allowedGuis.contains(Helper.getGuiName())) {
                line.updateMouseInteraction(mouseX, mouseY, currentX * scale, currentY * scale, textRenderer, scale, drawContext);
            }

            line.draw(drawContext, (int) currentX, (int) currentY, textRenderer);

            if (line.linebreak) {
                currentY += textRenderer.fontHeight + 1;
                currentX = x / scale;
            } else {
                currentX += textRenderer.getWidth(line.text);
            }
        }

        drawContext.getMatrices().pop();
    }

    private void drawDebugBox(DrawContext drawContext, int x, int y, int width, int height) {
        drawContext.drawBorder(x, y, width, height, new Color(255, 0, 0, 170).getRGB());
    }
}
