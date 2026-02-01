package com.me.coresmodule.mixin.ItemRendering;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.me.coresmodule.utils.render.CustomItem.CustomItemRender.shouldGlint;

@Mixin(Item.class)
public class GlintMixin2 {
    @Redirect(
            method = "hasGlint",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasEnchantments()Z")
    )
    private boolean redirectHasGlint2(ItemStack stack) {
        return shouldGlint(stack);
    }
}