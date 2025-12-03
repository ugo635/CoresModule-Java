package com.me.coresmodule.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;
import java.net.URI;

public class RequestHelper {
    public static JsonObject request(String link) {
        try {
            URL url = new URI(link).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
            }
            in.close();

            return JsonParser.parseString(content.toString()).getAsJsonObject();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void requestAsync(String link, Consumer<JsonObject> callback) {
        new Thread(() -> {
            JsonObject result = request(link);
            callback.accept(result);
        }).start();
    }
}