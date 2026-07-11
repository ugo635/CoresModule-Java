package com.me.coresmodule.features.farming;

import com.me.coresmodule.utils.events.Register;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import java.util.List;

import static com.me.coresmodule.CoresModule.mc;

public class HoldDirection {
    private static boolean isHolding = false;
    private static Direction activeDirection = null;
    private static KeyBinding toggleKey;
    private static GLFWKeyCallbackI previousKeyCallback;
    private static GLFWMouseButtonCallbackI previousMouseCallback;

    public static void register() {
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyBinding(
                Component.translatable("key.coresmodule.holdkey").getString(),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_END,
                new KeyBinding.Category(Identifier.of("coresmodule", "farming"))
        ));

        Register.onClientStart(args -> HoldDirection.main());
    }

    public static void main() {
        // Keyboard Input Hook
        previousKeyCallback = GLFW.glfwSetKeyCallback(mc.getWindow().getHandle(), (window, key, scancode, action, mods) -> {
            if (mc.currentScreen == null && action == GLFW.GLFW_PRESS) {
                int toggleKeyCode = InputUtil.fromTranslationKey(toggleKey.getBoundKeyTranslationKey()).getCode();
                int attackKeyCode = InputUtil.fromTranslationKey(mc.options.attackKey.getBoundKeyTranslationKey()).getCode();

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
        previousMouseCallback = GLFW.glfwSetMouseButtonCallback(mc.getWindow().getHandle(), (window, button, action, mods) -> {
            if (mc.currentScreen == null && isHolding && action == GLFW.GLFW_PRESS) {
                int attackButtonCode = InputUtil.fromTranslationKey(mc.options.attackKey.getBoundKeyTranslationKey()).getCode();
                if (button == attackButtonCode) { // If physical click on the mouse
                    cancelHold(false);
                }
            }
            if (previousMouseCallback != null) previousMouseCallback.invoke(window, button, action, mods);
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            // Cancel if in a menu
            if (mc.currentScreen != null) {
                if (isHolding) cancelHold(true);
                return;
            }

            // Hold
            if (isHolding && activeDirection != null) {
                activeDirection.getKey().setPressed(true);
                mc.options.attackKey.setPressed(true);
            }
        });
    }

    private static void cancelHold(boolean removeMouseHold) {
        isHolding = false;
        if (activeDirection != null) {
            activeDirection.getKey().setPressed(false); // Movement key
            activeDirection = null;
        }
        // Attack key
        if (removeMouseHold) mc.options.attackKey.setPressed(false);
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

        public KeyBinding getKey() {
            return switch (this) {
                case LEFT -> mc.options.leftKey;
                case RIGHT -> mc.options.rightKey;
                case FORWARD -> mc.options.forwardKey;
                case BACKWARD -> mc.options.backKey;
            };
        }

        public int getKeyboardKeyCode() {
            return InputUtil.fromTranslationKey(getKey().getBoundKeyTranslationKey()).getCode();
        }

        public boolean isPhysicalKeyPressed() {
            return InputUtil.isKeyPressed(mc.getWindow(), getKeyboardKeyCode());
        }
    }
}