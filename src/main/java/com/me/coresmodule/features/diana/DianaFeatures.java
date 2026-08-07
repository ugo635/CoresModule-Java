package com.me.coresmodule.features.diana;

import com.me.coresmodule.utils.helpers.ItemHelper;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.overlay.Overlay;
import com.me.coresmodule.utils.render.overlay.OverlayTextLine;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class DianaFeatures {
    static boolean ffTimerOn = false;
    static long startTime = -1;
    static long endTime = -1;
    static double remaining = -1;

    public static void register() {
        Overlay overlay = new Overlay("Fire Freeze Timer", 10.0f, 10.0f, 2.0f, List.of("Chat screen"));
        OverlayTextLine overlayComponent = new OverlayTextLine("");
        overlay.register();
        overlay.setCondition(() -> ffTimerOn);
        overlay.addLine(overlayComponent);

        UseItemCallback.EVENT.register((player, world, hand) -> {
            return updateTimer(player);
        });

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            return updateTimer(player);
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

            overlayComponent.text = "%s%.2fs".formatted(remaining <= 0 ? "§c" : "§a", remaining);
        });
    }

    @NonNull
    private static InteractionResult updateTimer(Player player) {
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
