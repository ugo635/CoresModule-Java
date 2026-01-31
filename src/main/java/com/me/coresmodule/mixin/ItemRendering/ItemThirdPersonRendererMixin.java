package com.me.coresmodule.mixin.ItemRendering;

import com.me.coresmodule.utils.render.CustomItem.CustomItemRender;
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
    private Object replaceHandThirdPersonItemStack(ItemStack stack, ComponentType componentType) {
        return CustomItemRender.replaceItemStack(stack).get(DataComponentTypes.ITEM_MODEL);
    }

    @ModifyArg(
            method = "update",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModel;update(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/item/ItemModelManager;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/HeldItemContext;I)V")
    )
    private ItemStack replaceHandThirdPersonItemStack1(ItemStack stack) {
        return CustomItemRender.replaceItemStack(stack);
    }
}
