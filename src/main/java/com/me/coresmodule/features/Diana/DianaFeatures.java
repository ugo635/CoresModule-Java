package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.overlay.Overlay;
import com.me.coresmodule.utils.render.overlay.OverlayTextLine;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

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

        Register.command("kingWarning", args -> {
            Helper.sleep(200, () -> {
                Chat.command("pc ⚠ Do NOT use Ranged/Magic damage on Kings // No Flaming Flay / No Term / No Crimson Stacks (Dominus) // Switch to Sorrow/Mythos armor and do the hit phase with melee only ⚠");
            });
        });

        Register.command("kingSpawn", args -> {
            Helper.sleep(200, () -> {
                Chat.command("pc Hi old man, give me your wool or I’m selling your brother’s soul to the devil—and yours too if I manage to grab it >:)");
            });
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
			if (ffTimerOn || remaining >= 0) return ActionResult.PASS;
            ItemStack item = player.getMainHandStack();
            if (ItemHelper.getItemName(item).contains("Fire Freeze Staff")) {
                ffTimerOn = true;
                startTime = System.currentTimeMillis();
                endTime = startTime + 10000;
            }

            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
			if (ffTimerOn || remaining >= 0) return ActionResult.PASS;
            ItemStack item = player.getMainHandStack();
            if (ItemHelper.getItemName(item).contains("Fire Freeze Staff")) {
                ffTimerOn = true;
                startTime = System.currentTimeMillis();
                endTime = startTime + 10000;
            }

            return ActionResult.PASS;
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
}
