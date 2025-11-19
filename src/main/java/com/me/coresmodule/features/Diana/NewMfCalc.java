package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.math.CmVectors;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.mc;

public class NewMfCalc {
    public static double additionalMf = 0;
    public static String profileId = "";
    public static void register() {
        try {
            FilesHandler.createFile("apiToken.txt");
            FilesHandler.createFile("profileId.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Register.onChatMessage(Pattern.compile("§8Profile ID: ([0-9a-fA-F]+(-[0-9a-fA-F]+)+)"), false, (msg, matcher) -> {
            profileId = matcher.group(1);
            Chat.chat("§a[Cm] §eProfile ID set to: §6" + profileId);
            try {
                FilesHandler.writeToFile("profileId.txt", profileId);
            } catch (IOException e) {
                Chat.chat("§c[Cm] §eFailed to save Profile ID to file!");
                throw new RuntimeException(e);
            }
        });

        Register.command("getAdditionalMf", args -> {
            Chat.chat(String.valueOf(additionalMf));
        }, "addMf");

        Register.command("getItemName", args -> {
            Chat.chat(ItemHelper.getHeldItemName());
        });

        Register.command("addTooltipToHeldItem", args -> {
            ItemHelper.addTooltip(ItemHelper.getHeldItem(), "§6§lHiiiii");
        });

        Register.command("getAllArmor", args -> {
            Chat.chat("§6 Armor mf: " + mfFromArmor());
        });

        Register.command("getCurrentItemLore", args -> {
            List<Text> lore = ItemHelper.getHeldItemTooltip();
            for (Text line : lore) {
                Chat.chat(TextHelper.getFormattedString(line));
            }
        });

        Register.command("getMfFromDae", args -> {
            Chat.chat("§6 Dae Bonus: " + mfFromHand());
        });

        Register.command("amtOfPlayersInLegion", args -> {;
            int count = playersInLegion();
            Chat.chat("§6 Players in Legion: " + count);
        });

        Register.command("amtOfMythos", args -> {
            Chat.chat("§6Mf From Mythos Armor: " + mfFromMythos());
        });

        Register.command("setAPIkey", args -> {
           String key = args[0];
            try {
                FilesHandler.writeToFile("apiToken.txt", key);
                Chat.chat("§aAPI Key set to: §e" + key);
            } catch (IOException e) {
                Chat.chat("§cFailted to set API Key: §e" + key);
                throw new RuntimeException(e);
            }
        });

        /*
        Register.command("getMyBe", args -> {
           List<String> wanted = List.of(
                   "cretan_bull_150",
                   "harpy_175",
                   "stranded_nymph_150",
                   "sphinx_750",
                   "gaia_construct_85",
                   "cretan_bull_70",
                   "harpy_325",
                   "manticore_1750",
                   "minos_hunter_200",
                   "cretan_bull_250",
                   "sphinx_1250",
                   "stranded_nymph_250",
                   "minos_champion_550",
                   "minos_inquisitor_1250",
                   "gaia_construct_325",
                   "king_minos_1750",
                   "siamese_lynx_200"
           ); // TODO: Get each mob with each level
        });
         */
    }

    /*
    Might count armor stands in it :/
     */
    public static int playersInLegion() {
        List<AbstractClientPlayerEntity> players = mc.world.getPlayers();
        int count = 0;
        for (AbstractClientPlayerEntity player : players) {
            Entity entity = mc.world.getEntityById(player.getId());
            CmVectors entityCoords = new CmVectors(entity.getX(), entity.getY(), entity.getZ());
            double distance = entityCoords.distanceTo(new CmVectors(mc.player.getX(), mc.player.getY(), mc.player.getZ()));
            if (distance <= 30) {
                count++;
            }
        }
        return count;
    }

    public static double mfFromMythos() {
        double count = 0.0;
        List<ItemStack> armor = ItemHelper.getArmorItems();
        for (ItemStack item : armor) {
            String name = ItemHelper.getItemName(item);
            if (name.contains("Mythos")) {
                count++;
            }
        }
        if (count <= 1) return 0;
        return 5 * count;
    }

    public static double mfFromHand() {
        String mf1;
        String mf2;
        String amt;

        // Get the Magic Find: +x
        mf1 = !Objects.equals(amt = ItemHelper.getValueFromLine(Pattern.compile("Magic Find: \\+(.*)"), ItemHelper.getHeldItemTooltip()), "") && !amt.isEmpty() ? amt : "0.0";
        // Get the +x✯ Magic Find (be tiers)
        mf2 = !Objects.equals(amt = ItemHelper.getValueFromLine(Pattern.compile("\\+([0-9]*\\.?[0-9]+)✯ Magic Find"), ItemHelper.getHeldItemTooltip()), "") && !amt.isEmpty() ? amt : "0.0";
        double mf = Double.parseDouble(mf1) + Double.parseDouble(mf2);
        return mf;
    }

    public static double mfFromArmor() {
        List<ItemStack> armor = ItemHelper.getArmorItems();
        String mf1;
        String mf2;
        String mf3;
        String mf4;
        String mf5;
        String amt;

        mf1 = !Objects.equals(amt = ItemHelper.getValueFromLine(Pattern.compile("Magic Find: \\+(.*)"), ItemHelper.getItemTooltip(armor.get(0))), "") && !amt.isEmpty() ? amt : "0.0";
        mf2 = !Objects.equals(amt = ItemHelper.getValueFromLine(Pattern.compile("Magic Find: \\+(.*)"), ItemHelper.getItemTooltip(armor.get(1))), "") && !amt.isEmpty() ? amt : "0.0";
        mf3 = !Objects.equals(amt = ItemHelper.getValueFromLine(Pattern.compile("Magic Find: \\+(.*)"), ItemHelper.getItemTooltip(armor.get(2))), "") && !amt.isEmpty() ? amt : "0.0";
        mf4 = !Objects.equals(amt = ItemHelper.getValueFromLine(Pattern.compile("Magic Find: \\+(.*)"), ItemHelper.getItemTooltip(armor.get(3))), "") && !amt.isEmpty() ? amt : "0.0";
        mf5 = !Objects.equals(amt = ItemHelper.getValueFromLine(Pattern.compile("\\+([0-9]*\\.?[0-9]+)✯ Magic Find ✿"), ItemHelper.getItemTooltip(armor.get(3))), "") && !amt.isEmpty() ? amt : "0.0";

        double mf = Double.parseDouble(mf1) + Double.parseDouble(mf2) + Double.parseDouble(mf3) + Double.parseDouble(mf4) + Double.parseDouble(mf5) + mfFromMythos();
        return mf;
    }

    /*
    public static void fetchBestiary(Consumer<JsonObject> callback) {
        String profileId;
        String key;
        String uuid;

        try {
            profileId = FilesHandler.getContent("profileId.txt").trim();
        } catch (IOException e) {
            Chat.chat("§cFailed to read profileId.txt!");
            callback.accept(null);
            return;
        }

        if (mc.player != null) {
            uuid = mc.player.getUuidAsString();
        } else {
            Chat.chat("§cPlayer is null!");
            callback.accept(null);
            return;
        }

        if (profileId.isEmpty()) {
            Chat.chat("§cProfile ID is not found!");
            callback.accept(null);
            return;
        }

        try {
            key = FilesHandler.getContent("apiToken.txt").trim();
        } catch (IOException e) {
            Chat.chat("§cFailed to read API Key from file!");
            callback.accept(null);
            return;
        }

        String finalLink = "https://api.hypixel.net/v2/skyblock/profile?key="
                + key + "&profile=" + profileId;

        RequestHelper.requestAsync(finalLink, response -> {
            if (response == null || response.isEmpty() || !response.has("success")
                    || !response.get("success").getAsBoolean()) {
                Chat.chat("§cFailed to get data from Hypixel API!");
                callback.accept(null);
                return;
            }

            try {
                JsonObject bestiary = response.getAsJsonObject("profile")
                        .getAsJsonObject("members")
                        .getAsJsonObject(uuid.replaceAll("-", "").trim())
                        .getAsJsonObject("bestiary");

                callback.accept(bestiary);
            } catch (Exception e) {
                Chat.chat("§cFailed to parse bestiary data!");
                callback.accept(null);
            }
        });
    }
     */
}