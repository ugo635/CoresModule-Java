package com.me.coresmodule.utils.render.overlay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class OverlayEditScreen extends Screen {

    private Overlay selectedOverlay = null;
    private boolean isDragging = false;
    private double lastMouseX = 0.0;
    private double lastMouseY = 0.0;

    public OverlayEditScreen() {
        super(Text.literal("CM_Overlay_Editor"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderDarkening(context);
        OverlayManager.render(context);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            selectedOverlay = OverlayManager.overlays.stream()
                    .filter(o -> o.isOverOverlay(mouseX, mouseY))
                    .findFirst()
                    .orElse(null);

            if (selectedOverlay != null) {
                isDragging = true;
                lastMouseX = mouseX;
                lastMouseY = mouseY;

                for (Overlay o : OverlayManager.overlays) {
                    o.selected = (o == selectedOverlay);
                }
            } else {
                for (Overlay o : OverlayManager.overlays) {
                    o.selected = false;
                }
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging && selectedOverlay != null) {
            selectedOverlay.x = selectedOverlay.x + (float) deltaX;
            OverlayData.overlays.get(selectedOverlay.name).x = selectedOverlay.x;
            selectedOverlay.y = selectedOverlay.y + (float) deltaY;
            OverlayData.overlays.get(selectedOverlay.name).y = selectedOverlay.y;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (selectedOverlay != null) {
            float newScale = (float) (selectedOverlay.scale + verticalAmount * 0.1f);
            if (newScale < 0.5f) newScale = 0.5f;
            if (newScale > 5.0f) newScale = 5.0f;
            selectedOverlay.scale = newScale;
            OverlayData.overlays.get(selectedOverlay.name).scale = newScale;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectedOverlay != null) {
            float step = 1f;
            if (keyCode == GLFW.GLFW_KEY_UP) {
                selectedOverlay.y -= step;
                OverlayData.overlays.get(selectedOverlay.name).y = selectedOverlay.y;
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                selectedOverlay.y += step;
                OverlayData.overlays.get(selectedOverlay.name).y = selectedOverlay.y;
            } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
                selectedOverlay.x -= step;
                OverlayData.overlays.get(selectedOverlay.name).x = selectedOverlay.x;
            } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                selectedOverlay.x += step;
                OverlayData.overlays.get(selectedOverlay.name).x = selectedOverlay.x;
            } else {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        super.removed();
        for (Overlay o : OverlayManager.overlays) {
            o.selected = false;
        }
        OverlayData.save();
    }
}
