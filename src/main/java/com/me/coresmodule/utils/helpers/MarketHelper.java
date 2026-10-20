package com.me.coresmodule.utils.helpers;

import com.me.coresmodule.utils.events.Register;
import org.json.JSONException;
import org.json.JSONObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MarketHelper {
    private static volatile HashMap<String, Object> bazaar = null;
    private static final int FIVE_MINUTES_IN_TICK = 20 * 60 * 5;

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Single daemon thread so refreshes never run on the render thread
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "CoresModule-MarketHelper");
        t.setDaemon(true);
        return t;
    });

    // Prevents overlapping refreshes if one is slow
    private static final AtomicBoolean updating = new AtomicBoolean(false);

    public static void register() {
        Register.onTick(FIVE_MINUTES_IN_TICK, args -> updateCache());

        updateCache();
    }

    /**
     * Blocking call: do NOT run this on the render thread.
     * Returns null if the request or parsing failed.
     * <p>
     * It is not recommended to use this to fetch an item's info.
     * You should use {@link #getItemInfo(String, Market)}
     */
    private static @Nullable HashMap<String, Object> getMarketInfo(Market market) {
        if (market == Market.AUCTION_HOUSE) throw new RuntimeException("Not implemented yet!");

        try {
            URI uri = market.getURI();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("[CoresModule] " + market + " returned HTTP " + response.statusCode());
                return null;
            }

            return new HashMap<>(new JSONObject(response.body()).toMap());

        } catch (JSONException e) {
            System.err.println("[CoresModule] Invalid JSON from " + market + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[CoresModule] Network error for " + market + ": " + e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.err.println("[CoresModule] Unexpected error for " + market + ": " + e);
        }

        return null;
    }

    private static void updateCache() {
        if (!updating.compareAndSet(false, true)) return; // refresh already running

        EXECUTOR.execute(() -> {
            try {
                HashMap<String, Object> result = getMarketInfo(Market.BAZAAR);
                if (result != null) bazaar = result; // keep last good data on failure
            } finally {
                updating.set(false);
            }
        });
    }

    public static @Nullable ItemInfo getItemInfo(String itemID, Market market) {
        if (market == Market.AUCTION_HOUSE) throw new RuntimeException("Not implemented yet!");

        HashMap<String, Object> item = getItem(itemID);
        if (item == null) return null;

        @SuppressWarnings("unchecked")
        HashMap<String, Object> quickStatus = (HashMap<String, Object>) item.get("quick_status");
        if (quickStatus == null) return null;

        return new ItemInfo(
                (String) quickStatus.get("productId"),
                (String) quickStatus.get("productId"),
                ((Number) quickStatus.get("buyPrice")).doubleValue(),
                ((Number) quickStatus.get("sellPrice")).doubleValue(),
                ((Number) quickStatus.get("sellOrders")).intValue(),
                ((Number) quickStatus.get("buyOrders")).intValue(),
                ((Number) quickStatus.get("sellMovingWeek")).intValue(),
                ((Number) quickStatus.get("buyMovingWeek")).intValue()
        );
    }

    @SuppressWarnings("unchecked")
    private static @Nullable HashMap<String, Object> getItem(String itemID) {
        HashMap<String, Object> data = bazaar; // local copy: the field may be swapped by the refresh thread
        if (data == null) return null; // not loaded yet
        if (!Boolean.TRUE.equals(data.get("success"))) return null; // If it failed

        HashMap<String, Object> products = (HashMap<String, Object>) data.get("products");
        if (products == null) return null;

        return (HashMap<String, Object>) products.get(itemID);
    }

    /**
     * Contains market information about an item.
     *
     * @param id The Item ID
     * @param name The Item Name
     * @param instaSellPrice The price when insta-selling the item (or buy order)
     * @param instaBuyPrice The price when insta-buying the item (or sell order)
     * @param sellOffers How many sell offers there are for this item
     * @param buyOffers How many buy offers there are for this item
     * @param soldThisWeek How many of this item was sold the past 7d
     * @param boughtThisWeek How many of this item was bought the past 7d
     */
    public record ItemInfo(
            String id,
            String name,
            double instaSellPrice,
            double instaBuyPrice,
            int sellOffers,
            int buyOffers,
            int soldThisWeek,
            int boughtThisWeek
    ) {
        @Override
        public @NonNull String toString() {
            return String.format(
                    "%s (%s) - Insta-Sell: %.2f, Insta-Buy: %.2f, Sell Offers: %d, Buy Offers: %d, Sold This Week: %d, Bought This Week: %d",
                    name,
                    id,
                    instaSellPrice,
                    instaBuyPrice,
                    sellOffers,
                    buyOffers,
                    soldThisWeek,
                    boughtThisWeek
            );
        }
    }

    public enum Market {
        BAZAAR("https://api.hypixel.net/skyblock/bazaar"),
        AUCTION_HOUSE("https://api.hypixel.net/skyblock/auctions");

        public final String uri;

        Market(String uri) {
            this.uri = uri;
        }

        public URI getURI() throws URISyntaxException {
            return new URI(uri);
        }
    }
}