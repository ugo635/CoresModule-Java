package com.me.coresmodule.mixin.ItemRendering;

import com.me.coresmodule.CoresModule;
import com.me.coresmodule.utils.render.CustomItemRender;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemModelManager.class)
public class ItemThirdPersonRendererMixin {
    // Replace the item texture when holding it in the third person view


    @Redirect(
            method = "update",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;get(Lnet/minecraft/component/ComponentType;)Ljava/lang/Object;")
    )
    private Object replaceHandThirdPersonItemStack3(ItemStack stack, ComponentType componentType) {
        return CustomItemRender.replaceItemStack(stack).get(DataComponentTypes.ITEM_MODEL);
    }
}
