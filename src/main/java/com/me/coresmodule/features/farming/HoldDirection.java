package com.me.coresmodule.features.farming;

import com.me.coresmodule.utils.events.Register;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import static com.me.coresmodule.CoresModule.mc;

public class HoldDirection {
    private static boolean isHolding = false;
    private static Direction activeDirection = null;
    private static KeyBinding toggleKey;
    private static GLFWKeyCallbackI previousCallback;

    public static void register() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.coresmodule.holdkey",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_END,
                new KeyBinding.Category(Identifier.of("coresmodule", "farming"))
        ));

        Register.onClientStart(args -> HoldDirection.main());
    }

    public static void main() {
        // Input Hook: Registers a callback that runs on every physical key event
        previousCallback = GLFW.glfwSetKeyCallback(mc.getWindow().getHandle(), (window, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                int toggleKeyCode = InputUtil.fromTranslationKey(toggleKey.getBoundKeyTranslationKey()).getCode();

                // Activation Logic
                if (key == toggleKeyCode && !isHolding) {
                    for (Direction dir : Direction.values()) {
                        if (dir.isPhysicalKeyPressed()) {
                            activeDirection = dir;
                            isHolding = true;
                            break;
                        }
                    }
                }
                // Cancel Logic
                else if (isHolding && activeDirection != null && key == activeDirection.getKeyboardKeyCode()) {
                    isHolding = false;
                    activeDirection.getKey().setPressed(false);
                    activeDirection = null;
                }
            }

            // Always call the previous callback to ensure other mods don't break
            if (previousCallback != null) previousCallback.invoke(window, key, scancode, action, mods);
        });

        Register.onTick(1, args -> {
            // Hold
            if (isHolding && activeDirection != null) {
                activeDirection.getKey().setPressed(true);
            }
        });
    }

    public enum Direction {
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