package com.me.coresmodule.mixin;

import com.me.coresmodule.CoresModule;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "close", at = @At("RETURN"))
    private void onGameRendererClose(CallbackInfo ci) {
        CoresModule.getInstance().close();
    }
}