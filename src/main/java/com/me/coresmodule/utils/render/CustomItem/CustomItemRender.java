package com.me.coresmodule.utils.render.CustomItem;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import net.minecraft.item.ItemStack;


import static com.me.coresmodule.CoresModule.overrides;

public class CustomItemRender {
    public static ItemStack replaceItemStack(ItemStack stack) {
        String uuid = ItemHelper.getComponent(stack, "CmUuid", "String");
        if (uuid == null || !overrides.containsKey(uuid)) return stack;
        return overrides.get(uuid).second;
    }

    public static boolean shouldGlint(ItemStack stack) {
        Boolean glint = ItemHelper.<Boolean>getComponent(stack, "CmGlint", "Boolean");
        return glint != null && glint;
    }

    public static void register() {
        Register.command("nullity", args -> {
            Chat.chat(String.valueOf(ItemHelper.<String>getComponent(ItemHelper.getHeldItem(), "CmUuid", "String")));
            Chat.chat(String.valueOf(ItemHelper.<Boolean>getComponent(ItemHelper.getHeldItem(), "CmGlint", "Boolean")));
        });
    }


}