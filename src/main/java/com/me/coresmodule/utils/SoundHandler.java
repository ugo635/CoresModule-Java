package com.me.coresmodule.utils;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;

import static com.me.coresmodule.CoresModule.MOD_ID;
import static com.me.coresmodule.CoresModule.mc;

public class SoundHandler {
    public static void register() {
        registerSound("emergencymeeting");
        registerSound("ding");
    }

    public static void registerSound(String path) {
        Registry.register(
                BuiltInRegistries.SOUND_EVENT,
                Identifier.fromNamespaceAndPath(MOD_ID, path),
                SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(MOD_ID, path))
        );
    }

    /**
     * Custom sounds only
     *
     * <pre><code>
     *      Ex:
     *      SoundHandler.playCustomSound("ding");
     * </code></pre>
     */
    public static void playCustomSound(String path) {
        SoundEvent sound = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(MOD_ID, path));
        if (mc.level != null && mc.player != null) {
            mc.level.playSound(mc.player, mc.player.blockPosition(), sound, SoundSource.MASTER, 1.0f, 1.0f);
        }
    }

    /**
     * Minecraft sounds only
     */
    public static void playMinecraftSound(String path) {
        SoundEvent sound = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("minecraft", path));
        if (mc.level != null && mc.player != null) {
            mc.level.playSound(mc.player, mc.player.blockPosition(), sound, SoundSource.MASTER, 1.0f, 1.0f);
        }
    }
}
