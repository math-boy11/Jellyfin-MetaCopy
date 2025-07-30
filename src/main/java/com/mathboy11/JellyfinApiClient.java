package com.mathboy11;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class JellyfinApiClient {
    private final String serverUrl;
    private final String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();

    public JellyfinApiClient(String serverUrl, String apiKey) {
        this.serverUrl = serverUrl;
        this.apiKey = apiKey;
    }

    public HashMap<String, Object> getItemMetadata(String userId, String itemId) throws IOException, InterruptedException {
        // Make the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/Users/" + userId + "/Items/" + itemId))
                .header("X-Emby-Token", apiKey)
                .GET()
                .build();

        // Send the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Catch Jellyfin API errors
        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode()  + " while getting item metadata");
        }

        // Parse the response
        return JacksonUtil.getObjectMapper().readValue(response.body(), new TypeReference<>() {
        });
    }

    public ArrayList<HashMap<String, Object>> getItemImageList(String itemId) throws IOException, InterruptedException {
        // Make the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/Items/" + itemId + "/Images"))
                .header("X-Emby-Token", apiKey)
                .GET()
                .build();

        // Send the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Catch Jellyfin API errors
        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode()  + " while getting image list");
        }

        // Parse the response
        return JacksonUtil.getObjectMapper().readValue(response.body(), new TypeReference<>() {
        });
    }

    public byte[] getItemImage(String itemId, String imageType, Integer imageIndex) throws IOException, InterruptedException {
        // Make the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/Items/" + itemId + "/Images/" + imageType + "/" + imageIndex))
                .header("X-Emby-Token", apiKey)
                .GET()
                .build();

        // Send the request
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        // Catch Jellyfin API errors
        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode()  + " while downloading image");
        }

        return response.body();
    }

    public void setItemMetadata(String itemId, HashMap<String, Object> metadata) throws IOException, InterruptedException {
        // Convert the metadata to a JSON string
        String jsonData = JacksonUtil.getObjectMapper().writeValueAsString(metadata);

        // Make the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/Items/" + itemId))
                .header("X-Emby-Token", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();

        // Send the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Catch Jellyfin API errors
        if (response.statusCode() != 204) {
            throw new RuntimeException("HTTP " + response.statusCode()  + " while setting item metadata");
        }
    }

    public void setItemImage(String itemId, String imageType, Integer imageIndex, byte[] imageData) throws IOException, InterruptedException {
        // Find the MIME type
        String mimeType = TikaUtil.getTika().detect(imageData);

        // Convert the image data to base66
        String imageDataEncoded = Base64.getEncoder().encodeToString(imageData);

        // Make the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/Items/" + itemId + "/Images/" + imageType + "/" + imageIndex))
                .header("X-Emby-Token", apiKey)
                .header("Content-Type", mimeType)
                .POST(HttpRequest.BodyPublishers.ofString(imageDataEncoded))
                .build();

        // Send the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Catch Jellyfin API errors
        if (response.statusCode() != 204) {
            throw new RuntimeException("HTTP " + response.statusCode()  + " while setting item image");
        }
    }

    public void deleteItemImage(String itemId, String imageType) throws IOException, InterruptedException {
        // Make the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/Items/" + itemId + "/Images/" + imageType))
                .header("X-Emby-Token", apiKey)
                .DELETE()
                .build();

        // Send the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Catch Jellyfin API errors
        if (response.statusCode() != 204) {
            throw new RuntimeException("HTTP " + response.statusCode()  + " while deleting item image");
        }
    }
}
