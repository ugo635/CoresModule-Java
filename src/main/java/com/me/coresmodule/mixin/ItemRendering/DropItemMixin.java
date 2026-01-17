package com.me.coresmodule.mixin.ItemRendering;

import com.me.coresmodule.utils.render.CustomItem.CustomItemRender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntity.class)
public class DropItemMixin {
    @ModifyArg(
            method = "dropItem",
            at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"),
            index = 0
    )
    private ItemStack dropItem(ItemStack stack) {
        return CustomItemRender.replaceItemStack(stack);
    }
}
