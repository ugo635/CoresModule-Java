package com.me.coresmodule.utils.helpers;

import com.me.coresmodule.utils.events.Register;
import org.json.JSONObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class MarketHelper {
    private static HashMap<String, Object> bazaar;

    public static void register() {
        final int FIVE_MINUTES_IN_TICK = 20 * 60 * 5;
        Register.onTick(FIVE_MINUTES_IN_TICK, args -> updateCache());

        updateCache();
    }

    /**
     * It is not recommended to use this to fetch an item's info.
     * You should use {@link #getItemInfo(String, Market)}
     */
    public static @NonNull HashMap<String, Object> getMarketInfo(Market market) {
        HttpClient client = HttpClient.newHttpClient();
        if (market == Market.AUCTION_HOUSE) throw new RuntimeException("Not implemented yet!");
        try {
            URI uri = market.getURI();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            String response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .join();

            HashMap<String, Object> bazaar = (HashMap<String, Object>) new JSONObject(response).toMap();

            return bazaar;

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    private static void updateCache() {
        bazaar = getMarketInfo(Market.BAZAAR);
    }

    public static @Nullable ItemInfo getItemInfo(String itemID, Market market) {
        if (market == Market.AUCTION_HOUSE) throw new RuntimeException("Not implemented yet!");

        HashMap<String, Object> item = getItem(itemID);
        if (item == null) return null;

        HashMap<String, Object> quickStatus = (HashMap<String, Object>) item.get("quick_status");

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

    private static HashMap<String, Object> getItem(String itemID) {
        if (!((boolean) bazaar.get("success"))) return null; // If it failed

        HashMap<String, Object> products = (HashMap<String, Object>) bazaar.get("products");
        HashMap<String, Object> itemInfoMap = (HashMap<String, Object>) products.getOrDefault(itemID, null);

        return itemInfoMap;
    }

    /**
     * Contains market information about an item.
     *
     * @param id The Item ID
     * @param name The Item Name
     * @param instaSellPrice The price when insta-selling the item
     * @param instaBuyPrice The price when insta-buying the item
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
