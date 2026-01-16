package com.me.coresmodule.utils.render;

import com.me.coresmodule.CoresModule;
import com.me.coresmodule.utils.ItemHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class CustomItemRender {
    public static ItemStack replaceItemStack(ItemStack stack) {
        if (CoresModule.overrideItemFrom != null && stack == CoresModule.overrideItemFrom) {
            return ItemHelper.markNbt(new ItemStack(CoresModule.overrideItemTo), "CoresModuleGlint", true);
        }
        return stack;
    }

    public static boolean shouldGlint(ItemStack stack) {
        var data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) return false;
        return data.copyNbt().getBoolean("CoresModuleGlint").isPresent() ? data.copyNbt().getBoolean("CoresModuleGlint").get() : false;
    }

}