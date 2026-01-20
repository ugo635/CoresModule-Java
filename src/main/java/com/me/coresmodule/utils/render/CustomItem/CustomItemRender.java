package com.me.coresmodule.utils.render.CustomItem;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import net.minecraft.item.ItemStack;


import static com.me.coresmodule.CoresModule.overrides;

public class CustomItemRender {
    public static ItemStack replaceItemStack(ItemStack stack) {
        String uuid = ItemHelper.getUUID(stack);
        if (uuid == null || !overrides.containsKey(uuid)) return stack;
        return overrides.get(uuid).second;
    }

    public static boolean shouldGlint(ItemStack stack) {
        if (stack == null) return false;
        if (!overrides.containsKey(ItemHelper.getUUID(stack))) return false;
        Boolean glint = overrides.get(ItemHelper.getUUID(stack)).third;
        return glint != null && glint;
    }

    public static void register() {}

}