package com.me.coresmodule.mixin.ItemRendering;

import com.me.coresmodule.utils.render.CustomItem.CustomItemRender;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemRenderer.class)
public class ItemFirstPersonRendererMixin {
    // For this.itemModelManager.clearAndUpdate(this.itemRenderState, stack, displayContext, world, entity, seed);
    // Replace the item texture when holding it in the first person view
    @ModifyArg(
            method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/item/ItemModelManager;clearAndUpdate(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V"),
            index = 1
    )
    private ItemStack replaceFirstPersonHandItemStack(ItemStack stack) {
        return CustomItemRender.replaceItemStack(stack);
    }

}
