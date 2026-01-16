package com.me.coresmodule.mixin.ItemRendering;

import com.me.coresmodule.utils.render.CustomItem.CustomItemRender;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HandledScreen.class)
public class ItemGuiRendererMixin {
    // For context.drawItem(stack, x, y);
    // Replace the stack value in drawItem, makes the inventory show the modified texture
    @ModifyArg(
            method = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawItem(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;II)V"),
            index = 0
    )
    private ItemStack replaceGuiItemStack(ItemStack stack) {
        return CustomItemRender.replaceItemStack(stack);
    }


    // For context.drawStackOverlay(this.textRenderer, stack, x, y - (this.touchDragStack.isEmpty() ? 0 : 8), amountText);
    // May be usefull, not sure
    /*
    @ModifyArg(
            method = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawItem(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"),
            index = 1
    )
    private ItemStack replaceGuiItemStack2(ItemStack stack) {
        return CustomItemRender.replaceItemStack(stack);
    }
     */


    // For drawSlot(DrawContext context, Slot slot) replaces the stack from slot.getStack()
    // Replace the stack value in drawStackOverlay, makes the inventory show the modified texture
    @Redirect(
            method = "drawSlot",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;")
    )
    private ItemStack replaceGuiItemStack2(Slot slot) {
        ItemStack stack = slot.getStack();
        return CustomItemRender.replaceItemStack(stack);
    }

}
