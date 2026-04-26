package com.me.coresmodule.utils;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Request {
    public static CompletableFuture<HttpResponse<String>> fetch(String link, Type type) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .header("User-Agent", "Mozilla/5.0")
                .method(type.name(), HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }

    public static CompletableFuture<HttpResponse<String>> fetch(String link) {
        return fetch(link, Type.GET);
    }

    public enum Type {
        GET,
        POST,
        PUT
    }

    /**
     * Example usage
     */
    public static void main(String[] args) {
        Request.fetch("https://example.com")
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
    }
}