package com.me.coresmodule.mixin.ItemRendering;

import com.me.coresmodule.CoresModule;
import com.me.coresmodule.utils.render.CustomItemRender;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameHud.class)
public class ItemHotbarRendererMixin {
    // For context.drawItem(livingEntity, stack, x, y);
    // Replace the stack value in drawItem, makes the hotbar show the modified texture
    @ModifyArg(
            method = "renderHotbarItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"),
            index = 1
    )
    private ItemStack replaceHotbarItemStack(ItemStack stack) {
        return CustomItemRender.replaceItemStack(stack);
    }

    // For context.drawStackOverlay(this.textRenderer, stack, x, y, amountText);
    // Replace the stack value in drawStackOverlay, makes the hotbar show the modified texture
    @ModifyArg(
            method = "renderHotbarItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"),
            index = 1
    )
    private ItemStack replaceHotbarItemStack2(ItemStack stack) {
        return CustomItemRender.replaceItemStack(stack);
    }
}
