package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.events.EventBus.EventBus;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.math.CmVectors;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.mc;
import static com.me.coresmodule.utils.TextHelper.formattedString;

public class MfCalcHelper {
    private static final Map<Integer, ArmorStandEntity> trackedEntities = new HashMap<>();
    private static final Set<Integer> defeated = new HashSet<>();
    private static final EventBus EVENT_BUS = new EventBus();
    /*
    Might count armor stands in it :/
    */
    public static int playersInLegion() {
        List<AbstractClientPlayerEntity> players = mc.world.getPlayers();
        int count = 0;
        for (AbstractClientPlayerEntity player : players) {
            Entity entity = mc.world.getEntityById(player.getId());
            if (!(entity != null && entity.isAlive()) || entity == mc.player || entity.getWorld() != mc.world) continue;
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
     * Shuriken Part
     */


    public static void register() {
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ArmorStandEntity armorstand) {
                trackedEntities.put(entity.getId(), armorstand);
            }
        });

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof ArmorStandEntity living) {
                trackedEntities.remove(entity.getId());
                defeated.remove(entity.getId());
                NewMfCalc.shuriken = false;
            }
        });

        Register.onTick(1, entity -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            Iterator<Map.Entry<Integer, ArmorStandEntity>> iterator = trackedEntities.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, ArmorStandEntity> entry = iterator.next();
                Integer id = entry.getKey();
                ArmorStandEntity armorStand = entry.getValue();

                if (!armorStand.isAlive() || armorStand.getWorld() != world) {
                    iterator.remove();
                    defeated.remove(id);
                    continue;
                }
                checkEntity(armorStand, id);
            }
        });
    }

    private static boolean hasShuriken(String name) {
        /*
        List<String> shuriNames = List.of(
            "creatan bull",
            "harpy",
            "nymph",
            "sphinx",
            "gaia construct",
            "manticore",
            "minos hunter",
            "minos champion",
            "minos inquisitor",
            "king minos",
            "siamese lynx"
        );

         */
        return name.contains("✯")/*&& shuriNames.contains(name.toLowerCase())*/;
    }

    private static void checkEntity(ArmorStandEntity entity, Integer id) {
        String name = entity.getCustomName() != null
                ? formattedString(entity.getCustomName())
                : formattedString(entity.getName());

        if (!defeated.contains(id) && hasShuriken(name)) {
            NewMfCalc.shuriken = true;
        }
    }
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
       ); // TODO: Get each mob with each level, search with .startsWith
    });
*/

