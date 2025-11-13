package com.me.coresmodule.utils;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static com.me.coresmodule.CoresModule.MOD_ID;
import static com.me.coresmodule.CoresModule.mc;

public class SoundHandler {
    public static void register() {
        registerSound("emergencymeeting");
    }

    public static void registerSound(String path) {
        Registry.register(Registries.SOUND_EVENT, Identifier.of(MOD_ID, path),
                SoundEvent.of(Identifier.of(MOD_ID, path)));
    }

    public static void playSound(String path) {
        SoundEvent sound = SoundEvent.of(Identifier.of(MOD_ID, path));
        if (mc.world != null) {
            mc.world.playSound(mc.player, mc.player.getBlockPos(), sound, SoundCategory.MASTER, 1.0f, 1.0f);
        }
    }
}
