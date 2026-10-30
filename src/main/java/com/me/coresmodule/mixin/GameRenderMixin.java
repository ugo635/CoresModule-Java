package com.me.coresmodule.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import com.me.coresmodule.utils.events.EventBus.EventBus;
import com.me.coresmodule.utils.events.impl.AfterHudRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GameRenderMixin {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/client/DeltaTracker;ZZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Hud;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V",
                    shift = At.Shift.AFTER
            )
    )

    private void afterHudRender(
            DeltaTracker deltaTracker,
            boolean shouldRenderLevel,
            boolean resourcesLoaded,
            CallbackInfo ci,
            @Local GuiGraphicsExtractor graphics
    ) {
        EventBus.emit(new AfterHudRenderer(graphics));
    }
}