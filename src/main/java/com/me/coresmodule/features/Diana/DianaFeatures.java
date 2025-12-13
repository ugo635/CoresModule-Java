package com.me.coresmodule.features.Diana;

import com.me.coresmodule.settings.categories.Diana;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.overlay.Overlay;
import com.me.coresmodule.utils.render.overlay.OverlayTextLine;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

import java.util.List;

public class DianaFeatures {
    static boolean ffTimerOn = false;
    static double startTime = 0;
    static double endTime = 0;
    public static void register() {
        Overlay overlay = new Overlay("§dFire Freeze Timer", 10.0f, 10.0f, 2.0f, List.of("Chat screen", "Crafting"));
        OverlayTextLine overlayText = new OverlayTextLine("");
        overlay.register();
        overlay.setCondition(() -> Diana.ffTimer.get() && ffTimerOn);
        overlay.addLine(overlayText);

        Register.command("kingWarning", args -> {
           Helper.sleep(200, () -> {
               Chat.chat("⚠ Do NOT use Ranged/Magic damage on Kings // No Flaming Flay / No Term / No Crimson Stacks (Dominus) // Switch to Sorrow/Mythos armor and do the hit phase with melee only ⚠");
           });
        });

        Register.command("kingSpawn", args -> {
            Helper.sleep(200, () -> {
                Chat.chat("Hi old man, give me your wool or I’m selling your brother’s soul to the devil—and yours too if I manage to grab it >:)");
            });
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack item = player.getMainHandStack();
            String itemName = ItemHelper.getItemName(item);
            if (itemName.contains("Fire Freeze Staff")) {
                ffTimerOn = true;
                startTime = System.currentTimeMillis();
                endTime = startTime + 10000;
                Helper.exactSleep(10000, () -> {
                    ffTimerOn = false;
                    startTime = -1;
                    endTime = -1;
                });
            }
            return ActionResult.PASS;
        });

        Register.onTick(2, args -> {
            if (endTime - startTime < 0) return;
            startTime = System.currentTimeMillis();
            overlayText.text = "§a%.2fs".formatted(endTime - startTime);
        });
    }
}