package com.me.coresmodule.utils.render;

import com.me.coresmodule.CoresModule;
import com.me.coresmodule.utils.ItemHelper;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.me.coresmodule.CoresModule.*;

public class CustomItemRender {
    public static final ComponentType<String> UuidComponent = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(MOD_ID, "uuid"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING)
                    .packetCodec(PacketCodecs.STRING)
                    .build());

    public static final ComponentType<Boolean> CmGlint = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(MOD_ID, "coresmoduleglint"),
            ComponentType.<Boolean>builder()
                    .codec(Codec.BOOL)
                    .packetCodec(PacketCodecs.BOOLEAN)
                    .build());

    public static ItemStack replaceItemStack(ItemStack stack) {
        String uuid = stack.getOrDefault(UuidComponent, null);
        System.out.println("Uuid null? " + (uuid == null));
        if (uuid == null) {
            return stack;
        }

        System.out.println("Override.constainsKey() == true? " + overrides.containsKey(uuid));
        if (overrides.containsKey(uuid)) {
            return ItemHelper.markNbt(overrides.get(uuid).second, CmGlint, overrides.get(uuid).third);
        }

        return stack;
    }

    public static boolean shouldGlint(ItemStack stack) {
        var data = stack.get(CmGlint);
        if (data == null) return false;
        return data;
    }

    public static void register() {} // Fix Registry is alrd frozen error
}