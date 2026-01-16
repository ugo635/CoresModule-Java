package com.me.coresmodule.utils.render;
import com.me.coresmodule.utils.ItemHelper;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;



import com.me.coresmodule.CoresModule;
import com.me.coresmodule.utils.ItemHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.me.coresmodule.CoresModule.MOD_ID;
import static com.me.coresmodule.CoresModule.overrides;

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
        String uuid = String.valueOf(stack.getOrDefault(UuidComponent, null));
        if (uuid.equals("null") || !overrides.containsKey(uuid)) return stack;
        return overrides.get(uuid).second;
    }

    public static boolean shouldGlint(ItemStack stack) {
        var glint = stack.getOrDefault(CmGlint, null);
        return glint != null && glint;
    }

    public static void register() {}

}