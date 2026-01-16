package com.me.coresmodule.mixin.ItemRendering;

import com.me.coresmodule.CoresModule;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.me.coresmodule.utils.render.CustomItemRender.shouldGlint;

@Mixin(ItemStack.class)
public class GlintMixin {
    @Inject(
        method = "hasGlint",
        at = @At(value = "RETURN"),
        cancellable = true
    )
    private void redirectHasGlint(CallbackInfoReturnable<Boolean> cir) {
        if (shouldGlint((ItemStack) (Object) this)) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(cir.getReturnValue());
        }
    }
}
