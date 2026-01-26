package com.me.coresmodule.utils.render.CustomItem;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.Tuples.Quadruple;
import com.me.coresmodule.utils.Tuples.Triple;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


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

    public static void replaceItem(String itemID) {
        ItemStack heldItem = ItemHelper.getHeldItem();
        String Uuid = ItemHelper.getUUID(heldItem);
        ItemStack overrideItemTo = ItemHelper.createSecond(new ItemStack(Registries.ITEM.get(Identifier.of(itemID))), Uuid);
        boolean overrideItemToGlintBool = heldItem.hasGlint();

        overrides.put(Uuid, new Quadruple<>(
                heldItem,
                overrideItemTo,
                overrideItemToGlintBool,
                ItemHelper.getFormattedItemName(heldItem)
        ));

        SaveAndLoad.save();
    }

    public static boolean canReplace(String indentifier) {
        return Registries.ITEM.containsId(Identifier.of(indentifier));
    }

    public static Text replaceItemName(ItemStack stack) {
        String uuid = ItemHelper.getUUID(stack);
        if (uuid == null || !overrides.containsKey(uuid)) return stack.getName();
        return Text.of(overrides.get(uuid).fourth);
    }

    public static void register() {}

}