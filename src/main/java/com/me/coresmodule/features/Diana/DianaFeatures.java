package com.me.coresmodule.features.Diana;

import com.me.coresmodule.settings.categories.Diana;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.overlay.Overlay;
import com.me.coresmodule.utils.render.overlay.OverlayTextLine;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.List;

import static com.me.coresmodule.settings.categories.Diana.MfOverlay;
import static com.me.coresmodule.settings.categories.Diana.ffTimer;

public class DianaFeatures {
    static boolean ffTimerOn = false;
    public static void register() {
        Overlay overlay = new Overlay("Ff Timer", 10.0f, 10.0f, 1.0f, List.of("Chat screen", "Crafting"));
        OverlayTextLine overlayText = new OverlayTextLine("...");
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
                Helper.exactSleep(10000, () -> ffTimerOn = false);
            }
            return ActionResult.PASS;
        });
    }
}