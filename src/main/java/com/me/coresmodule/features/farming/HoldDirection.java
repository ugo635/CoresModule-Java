package com.me.coresmodule.features.farming;

import com.me.coresmodule.utils.events.Register;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import static com.me.coresmodule.CoresModule.mc;

public class HoldDirection {
    private static boolean isHolding = false;
    private static Direction activeDirection = null;
    private static KeyMapping toggleKey;
    private static GLFWKeyCallbackI previousKeyCallback;
    private static GLFWMouseButtonCallbackI previousMouseCallback;

    public static void register() {
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                Component.translatable("key.coresmodule.holdkey").getString(),
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_END,
                new KeyMapping.Category(Identifier.fromNamespaceAndPath("coresmodule", "farming"))
        ));

        Register.onClientStart(args -> HoldDirection.main());
    }

    public static void main() {
        // Keyboard Input Hook
        previousKeyCallback = GLFW.glfwSetKeyCallback(mc.getWindow().handle(), (window, key, scancode, action, mods) -> {
            if (mc.screen == null && action == GLFW.GLFW_PRESS) {
                int toggleKeyCode = InputConstants.getKey(toggleKey.saveString()).getValue();
                int attackKeyCode = InputConstants.getKey(mc.options.keyAttack.saveString()).getValue();

                // Activation Logic & Canceling Logic
                if (key == toggleKeyCode && !isHolding) {
                    for (Direction dir : Direction.values()) {
                        if (dir.isPhysicalKeyPressed()) {
                            activeDirection = dir;
                            isHolding = true;
                            break;
                        }
                    }
                } else if (isHolding && activeDirection != null && (isDirection(key) || key == attackKeyCode)) { // If phycical key pressed
                    cancelHold(false);
                }
            }
            if (previousKeyCallback != null) previousKeyCallback.invoke(window, key, scancode, action, mods);
        });

        // Mouse Input Hook
        previousMouseCallback = GLFW.glfwSetMouseButtonCallback(mc.getWindow().handle(), (window, button, action, mods) -> {
            if (mc.screen == null && isHolding && action == GLFW.GLFW_PRESS) {
                int attackButtonCode = InputConstants.getKey(mc.options.keyAttack.saveString()).getValue();
                if (button == attackButtonCode) { // If physical click on the mouse
                    cancelHold(false);
                }
            }
            if (previousMouseCallback != null) previousMouseCallback.invoke(window, button, action, mods);
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            // Cancel if in a menu
            if (mc.screen != null) {
                if (isHolding) cancelHold(true);
                return;
            }

            // Hold
            if (isHolding && activeDirection != null) {
                activeDirection.getKey().setDown(true);
                mc.options.keyAttack.setDown(true);
            }
        });
    }

    private static void cancelHold(boolean removeMouseHold) {
        isHolding = false;
        if (activeDirection != null) {
            activeDirection.getKey().setDown(false); // Movement key
            activeDirection = null;
        }
        // Attack key
        if (removeMouseHold) mc.options.keyAttack.setDown(false);
    }

    private static boolean isDirection(int keyCode) {
        Direction[] values = Direction.values();
        for (Direction direction : values) {
            if (direction.getKeyboardKeyCode() == keyCode) return true;
        }
        return false;
    }

    private enum Direction {
        LEFT, RIGHT, FORWARD, BACKWARD;

        public KeyMapping getKey() {
            return switch (this) {
                case LEFT -> mc.options.keyLeft;
                case RIGHT -> mc.options.keyRight;
                case FORWARD -> mc.options.keyUp;
                case BACKWARD -> mc.options.keyDown;
            };
        }

        public int getKeyboardKeyCode() {
            return InputConstants.getKey(getKey().saveString()).getValue();
        }

        public boolean isPhysicalKeyPressed() {
            return InputConstants.isKeyDown(mc.getWindow(), getKeyboardKeyCode());
        }
    }
}