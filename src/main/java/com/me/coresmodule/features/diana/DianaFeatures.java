package com.me.coresmodule.features.diana;

import com.me.coresmodule.utils.helpers.ItemHelper;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.hud.overlay.Overlay;
import com.me.coresmodule.utils.render.hud.overlay.OverlayTextLine;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DianaFeatures {
    static boolean ffTimerOn = false;
    static long startTime = -1;
    static long endTime = -1;
    static double remaining = -1;

    public static void register() {
        Overlay overlay = new Overlay("Fire Freeze Timer", 10.0f, 10.0f, 2.0f, List.of("Chat screen"));
        OverlayTextLine overlayText = new OverlayTextLine("");
        overlay.register();
        overlay.setCondition(() -> ffTimerOn);
        overlay.addLine(overlayText);

        UseItemCallback.EVENT.register((player, world, hand) -> {
            return updateTimer();
        });

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            return updateTimer();
        });

        Register.onTick(1, args -> {
            if (!ffTimerOn) return;

            long now = System.currentTimeMillis();
            remaining = (endTime - now) / 1000.0;

            if (remaining <= -5) { // Stops below -5s, between 5s and -5s is the time during which the mob is freezed
                ffTimerOn = false;
                remaining = -1;
                startTime = -1;
                endTime = -1;
                return;
            }

            overlayText.text = "%s%.2fs".formatted(remaining <= 0 ? "§c" : "§a", remaining);
        });
    }

    private static InteractionResult updateTimer() {
        if (ffTimerOn || remaining >= 0) return InteractionResult.PASS;
        ItemStack item = ItemHelper.getHeldItem();
        if (ItemHelper.getItemName(item).contains("Fire Freeze Staff")) {
            ffTimerOn = true;
            startTime = System.currentTimeMillis();
            endTime = startTime + 10000;
        }

        return InteractionResult.PASS;
    }
}
