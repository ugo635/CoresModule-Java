package com.me.coresmodule.mixin;


import com.me.coresmodule.utils.events.EventBus.EventBus;
import com.me.coresmodule.utils.events.impl.AfterHudRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    @Unique
    private GuiGraphicsExtractor cm$graphics;

    @ModifyVariable(method = "extractGui(Lnet/minecraft/client/DeltaTracker;ZZ)V", at = @At(value = "STORE"), name = "graphics")
    private GuiGraphicsExtractor graphicsExtractor(GuiGraphicsExtractor graphics) {
        cm$graphics = graphics;
        return graphics;
    }

    @Inject(method = "extractGui(Lnet/minecraft/client/DeltaTracker;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V", shift = At.Shift.AFTER))
    private void afterHudRender(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci) {
        EventBus.emit(new AfterHudRenderer(cm$graphics));
    }
}