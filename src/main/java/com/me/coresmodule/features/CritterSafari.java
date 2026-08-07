package com.me.coresmodule.features;

import com.me.coresmodule.settings.categories.General;
import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.helpers.AreaHelper;
import com.me.coresmodule.utils.helpers.Helper;
import com.me.coresmodule.utils.helpers.TextHelper;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.mc;

/**
 * Critter Safari shard counter.
 * <p>
 * Every CAPTURE! / LOOT SHARE! chat line bumps the named shard's count by one,
 * regardless of the quantity in the message. A "<player> entered Critter Safari!"
 * line starts a fresh run, and "SAFARI REWARD SUMMARY" ends it.
 */
public class CritterSafari {

    // ===================== SHARD DATA =====================

    private static final Map<String, String> SHARD_TO_AREA = new LinkedHashMap<>();
    private static final Map<String, Integer> EXACT_CAPS = new HashMap<>();
    private static final Map<String, Integer> DYNAMIC_CAPS = new HashMap<>();
    private static final Set<String> BIRD_SHARDS = Set.of("Macaw", "Parakeet", "Bluebird");
    private static final List<String> AREA_ORDER = List.of("Forest", "Icy", "Cavern", "Haunted");

    static {
        registerArea("Forest", "Hideonfloor", "Foxtrot", "Treefrog", "Woodchucker",
                "Honeybug", "Fluffling", "Bluebird", "Parakeet", "Macaw");
        registerArea("Icy", "Strongarm", "Polaris", "Troodon", "Shuddersquid",
                "Billygoat", "Tepid", "Nozzlenose", "Mantis Shrimp", "Wumpa");
        registerArea("Cavern", "Cavernfish", "Driftling", "Scrappy", "Flitter",
                "Chuckwalla", "Rockmite", "Snoozle", "Shyworm", "Gemzie");
        registerArea("Haunted", "Solsnatcher", "Hideonwall", "Duplico", "Litterbug",
                "Bloodbat", "Gazer", "Areita", "Gimmiegold", "Hideyho", "Doomspiral");

        // Green: this exact spawn count is fixed and therefore always complete.
        EXACT_CAPS.put("Wumpa", 1);
        EXACT_CAPS.put("Doomspiral", 1);
        EXACT_CAPS.put("Gemzie", 3);
        EXACT_CAPS.put("Hideyho", 1);
        EXACT_CAPS.put("Billygoat", 1);
        EXACT_CAPS.put("Troodon", 3);
        EXACT_CAPS.put("Gazer", 2);
        EXACT_CAPS.put("Scrappy", 3);

        // Gold: reaching this upper bound is definitely complete.
        DYNAMIC_CAPS.put("Hideonfloor", 3);
        DYNAMIC_CAPS.put("Foxtrot", 6);
        DYNAMIC_CAPS.put("Treefrog", 7);
        DYNAMIC_CAPS.put("Woodchucker", 6);
        DYNAMIC_CAPS.put("Honeybug", 6);
        DYNAMIC_CAPS.put("Fluffling", 3);
        DYNAMIC_CAPS.put("Strongarm", 8);
        DYNAMIC_CAPS.put("Polaris", 4);
        DYNAMIC_CAPS.put("Shuddersquid", 6);
        DYNAMIC_CAPS.put("Tepid", 12);
        DYNAMIC_CAPS.put("Nozzlenose", 4);
        DYNAMIC_CAPS.put("Mantis Shrimp", 6);
        DYNAMIC_CAPS.put("Cavernfish", 7);
        DYNAMIC_CAPS.put("Driftling", 6);
        DYNAMIC_CAPS.put("Flitter", 6);
        DYNAMIC_CAPS.put("Chuckwalla", 4);
        DYNAMIC_CAPS.put("Rockmite", 8);
        DYNAMIC_CAPS.put("Snoozle", 3);
        DYNAMIC_CAPS.put("Shyworm", 8);
        DYNAMIC_CAPS.put("Solsnatcher", 8);
        DYNAMIC_CAPS.put("Hideonwall", 4);
        DYNAMIC_CAPS.put("Duplico", 4);
        DYNAMIC_CAPS.put("Litterbug", 7);
        DYNAMIC_CAPS.put("Bloodbat", 4);
        DYNAMIC_CAPS.put("Areita", 4);
        DYNAMIC_CAPS.put("Gimmiegold", 5);
    }

    private static void registerArea(String area, String... shards) {
        for (String shard : shards) SHARD_TO_AREA.put(shard, area);
    }

    // ===================== REGEX =====================

    private static final String SHARD_ALTERNATION;
    private static final Pattern RESET_PATTERN =
            Pattern.compile("\\b(?<player>[A-Za-z0-9_]{1,16}) entered Critter Safari!");
    private static final Pattern SHARD_EVENT_PATTERN;
    private static final Pattern SPARKLING_ANNOUNCEMENT_PATTERN;
    private static final Pattern LOOT_CATCHER_PATTERN =
            Pattern.compile("\\sfrom\\s+(?<catcher>[A-Za-z0-9_]{1,16})\\s+(?:catching|finding)\\b");
    private static final Pattern FLOOR_DROP_PATTERN = Pattern.compile("\\bFLOOR DROP!");
    private static final Pattern SAFARI_REWARD_SUMMARY_PATTERN = Pattern.compile("\\bSAFARI REWARD SUMMARY\\b");

    static {
        List<String> namesByLengthDesc = new ArrayList<>(SHARD_TO_AREA.keySet());
        namesByLengthDesc.sort((a, b) -> b.length() - a.length());
        StringJoiner joiner = new StringJoiner("|");
        for (String name : namesByLengthDesc) joiner.add(Pattern.quote(name));
        SHARD_ALTERNATION = joiner.toString();

        SHARD_EVENT_PATTERN = Pattern.compile(
                "(?:(?<capturePrefix>CAPTURE!.*?(?:gained|gave you))|" +
                        "(?<lootPrefix>LOOT SHARE! You received))" +
                        "\\s+(?:(?<quantity>\\d+)x\\s+)?(?:an?\\s+)?" +
                        "(?<shard>" + SHARD_ALTERNATION + ")\\s+Shard\\b"
        );
        SPARKLING_ANNOUNCEMENT_PATTERN = Pattern.compile(
                "SPARKLING!\\s+(?<catcher>[A-Za-z0-9_]{1,16})\\s+caught\\s+" +
                        "a?\\s*SPARKLING\\s+(?<shard>" + SHARD_ALTERNATION + ")!"
        );
    }

    // ===================== STATE =====================

    private static final Map<String, Integer> counts = new LinkedHashMap<>();
    private static final Map<String, PlayerStats> players = new LinkedHashMap<>();

    private static int floorDrops = 0;
    private static int runsSinceShiny = 0;
    private static boolean runActive = false;
    private static boolean currentRunShiny = false;
    private static long runStartedAtMs = 0;

    private static class PlayerStats {
        int count = 0;
        Map<String, Integer> biomeCounts = new HashMap<>();

        String majorityBiome() {
            String best = null;
            int bestCount = -1;
            for (Map.Entry<String, Integer> entry : biomeCounts.entrySet()) {
                if (entry.getValue() > bestCount) {
                    bestCount = entry.getValue();
                    best = entry.getKey();
                }
            }
            return best;
        }
    }

    static {
        for (String shard : SHARD_TO_AREA.keySet()) counts.put(shard, 0);
    }

    // ===================== REGISTRATION =====================

    private static CritterSafariWidget widget;

    public static void register() {
        loadState();

        if (widget == null) {
            widget = new CritterSafariWidget(0.85f);
            widget.setCondition(() -> CritterSafari.isEnabled() && AreaHelper.isInSafari());
            widget.register();
        }

        Register.onChatMessage(message -> processLine(TextHelper.getUnFormattedString(message)));

        Register.command("csreset", ignored -> {
            resetRun(ownPlayerName());
            Chat.chat("§d[Cm] §fCritter Safari counters manually reset.");
        });
    }

    // ===================== SNAPSHOT API (for Widget renderer) =====================

    public record ShardView(String name, int count, String colorCode) {}
    public record AreaView(String name, int total, List<ShardView> shards) {}
    public record PlayerView(String name, int count, String biome) {}
    public record CritterSafariSnapshot(
            boolean runActive,
            boolean hasRun,
            int elapsedSeconds,
            String runHeaderText,
            int floorDrops,
            int runsSinceShiny,
            List<PlayerView> players,
            List<AreaView> areas
    ) {}

    /** Read-only snapshot intended for CritterSafariWidget render override. */
    public static synchronized CritterSafariSnapshot snapshotForWidget() {
        List<PlayerView> playerViews = new ArrayList<>();
        for (Map.Entry<String, PlayerStats> entry : players.entrySet()) {
            String biome = entry.getValue().majorityBiome();
            playerViews.add(new PlayerView(
                    entry.getKey(),
                    entry.getValue().count,
                    biome != null ? biome : "Unknown"
            ));
        }

        List<AreaView> areaViews = new ArrayList<>();
        for (String area : AREA_ORDER) {
            List<ShardView> shardViews = new ArrayList<>();
            int areaTotal = 0;

            for (Map.Entry<String, String> shardEntry : SHARD_TO_AREA.entrySet()) {
                if (!area.equals(shardEntry.getValue())) continue;
                String shard = shardEntry.getKey();
                int count = counts.getOrDefault(shard, 0);
                areaTotal += count;
                shardViews.add(new ShardView(shard, count, entryColor(shard, count)));
            }

            areaViews.add(new AreaView(area, areaTotal, List.copyOf(shardViews)));
        }

        boolean hasRun = runStartedAtMs != 0;
        int elapsed = elapsedSeconds();

        return new CritterSafariSnapshot(
                runActive,
                hasRun,
                elapsed,
                runHeaderText(),
                floorDrops,
                runsSinceShiny,
                List.copyOf(playerViews),
                List.copyOf(areaViews)
        );
    }

    public static synchronized boolean isEnabled() {
        return mc.player != null && General.critterSafari.get();
    }

    // ===================== CHAT PROCESSING =====================

    private static synchronized void processLine(String line) {
        Matcher resetMatcher = RESET_PATTERN.matcher(line);
        if (resetMatcher.find()) {
            resetRun(resetMatcher.group("player"));
            return;
        }

        if (!runActive) return;

        if (SAFARI_REWARD_SUMMARY_PATTERN.matcher(line).find()) {
            endRun();
            return;
        }

        if (FLOOR_DROP_PATTERN.matcher(line).find()) {
            floorDrops++;
            return;
        }

        Matcher sparklingMatcher = SPARKLING_ANNOUNCEMENT_PATTERN.matcher(line);
        if (sparklingMatcher.find()) {
            String shard = sparklingMatcher.group("shard");
            currentRunShiny = true;
            runsSinceShiny = 0;
            recordCapture(shard, sparklingMatcher.group("catcher"));
            Chat.chat("§d§l[Cm] SPARKLING §5" + shard + " §fcaught by §e" + sparklingMatcher.group("catcher") + "§f!");
            Helper.showTitle("§d§lSPARKLING!", "§5" + shard, 0, 25, 35);
            return;
        }

        Matcher eventMatcher = SHARD_EVENT_PATTERN.matcher(line);
        if (eventMatcher.find()) {
            String shard = eventMatcher.group("shard");
            String caughtBy;
            if (eventMatcher.group("capturePrefix") != null) {
                caughtBy = ownPlayerName();
            } else {
                Matcher catcherMatcher = LOOT_CATCHER_PATTERN.matcher(line);
                caughtBy = catcherMatcher.find() ? catcherMatcher.group("catcher") : "Unknown";
            }
            recordCapture(shard, caughtBy);
        }
    }

    private static void recordCapture(String shard, String caughtBy) {
        counts.merge(shard, 1, Integer::sum);

        PlayerStats stats = players.computeIfAbsent(caughtBy, ignored -> new PlayerStats());
        stats.count++;
        if (!BIRD_SHARDS.contains(shard)) {
            String biome = SHARD_TO_AREA.get(shard);
            if (biome != null) stats.biomeCounts.merge(biome, 1, Integer::sum);
        }
    }

    private static void resetRun(String startedBy) {
        counts.replaceAll((s, v) -> 0);
        players.clear();
        players.put(ownPlayerName(), new PlayerStats());
        floorDrops = 0;
        currentRunShiny = false;
        runActive = true;
        runStartedAtMs = System.currentTimeMillis();
    }

    private static void endRun() {
        runActive = false;
        if (currentRunShiny) {
            runsSinceShiny = 0;
        } else {
            runsSinceShiny++;
        }
        saveState();
    }

    private static String ownPlayerName() {
        return mc.player != null ? mc.player.getName().getString() : "You";
    }

    // ===================== SHARED FORMATTING / COLOR LOGIC =====================

    private static String runHeaderText() {
        String label = runActive || runStartedAtMs == 0 ? "Run" : "Last run";
        return "Critter Safari - " + label + " - " + formatDuration(elapsedSeconds());
    }

    private static int elapsedSeconds() {
        if (runStartedAtMs == 0) return 0;
        return (int) ((System.currentTimeMillis() - runStartedAtMs) / 1000);
    }

    private static String formatDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /** Mirrors red/neutral/green/gold completion coloring. */
    private static String entryColor(String shard, int count) {
        Integer exactCap = EXACT_CAPS.get(shard);
        if (exactCap != null) {
            return count == exactCap ? "§a" : "§c";
        }

        if (count == 0) {
            return shard.equals("Macaw") ? "§7" : "§c";
        }

        if (BIRD_SHARDS.contains(shard)) return "§7";

        Integer dynamicCap = DYNAMIC_CAPS.get(shard);
        if (dynamicCap != null && count >= dynamicCap) return "§6";

        return "§7";
    }

    // ===================== PERSISTENCE =====================

    private static void loadState() {
        try {
            FilesHandler.createFile("critterSafari.json");
            String content = FilesHandler.getContent("critterSafari.json").trim();
            if (!content.isEmpty() && !content.equals("{}")) {
                JSONObject json = new JSONObject(content);
                runsSinceShiny = json.optInt("runsSinceShiny", 0);
            } else {
                FilesHandler.writeToFile("critterSafari.json", "{}");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveState() {
        try {
            JSONObject json = new JSONObject();
            json.put("runsSinceShiny", runsSinceShiny);
            FilesHandler.writeToFile("critterSafari.json", json.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}