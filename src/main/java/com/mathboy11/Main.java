package com.mathboy11;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            // Load the config files
            Properties configData = ConfigLoader.loadConfig(args.length > 0 ? args[0] : null);
            HashMap<String, ArrayList<String>> fieldMapping = ConfigLoader.loadFieldMapping();

            // Create the Jellyfin API client
            JellyfinApiClient apiClient = new JellyfinApiClient(configData.getProperty("serverUrl"), configData.getProperty("apiKey"));

            // Get the list of source and target item IDs
            ArrayList<String> sourceItemIds = Arrays.stream(configData.getProperty("sourceItemIds").split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(ArrayList::new));

            ArrayList<String> targetItemIds = Arrays.stream(configData.getProperty("targetItemIds").split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(ArrayList::new));

            // Make sure the two lists have the same size
            if (sourceItemIds.size() != targetItemIds.size()) {
                throw new RuntimeException("Must have the same amount of source items and target items");
            }

            // Loop through the source item IDs list
            for (int i = 0; i < sourceItemIds.size(); i++) {
                String sourceItemId = sourceItemIds.get(i);
                String targetItemId = targetItemIds.get(i);

                System.out.println("Copying metadata.....");

                // Get the metadata for the source and target items
                HashMap<String, Object> sourceItemData = apiClient.getItemMetadata(configData.getProperty("userId"), sourceItemId);
                HashMap<String, Object> targetItemData = apiClient.getItemMetadata(configData.getProperty("userId"), targetItemId);

                // Get the list of fields that we are transferring for the source and target
                ArrayList<String> sourceItemFields = fieldMapping.get((String) sourceItemData.get("Type"));
                ArrayList<String> targetItemFields = fieldMapping.get((String) targetItemData.get("Type"));

                // Check if the type of the items are supported
                if (sourceItemFields == null) {
                    throw new RuntimeException(sourceItemData.get("Type") + " type is unsupported. Please use a different source item.");
                } else if (targetItemFields == null) {
                    throw new RuntimeException(targetItemData.get("Type") + " type is unsupported. Please use a different target item.");
                }

                // Loop through all the target fields
                for (String field : targetItemFields) {
                    // Check if the source doesn't have the field
                    if (!sourceItemFields.contains(field)) {
                        // Clear the target field by finding the type and setting it to it's appropriate empty value because Jellyfin will sometimes not clear it if it's null or gone
                        Object value = targetItemData.get(field);

                        switch (value) {
                            case Map map -> targetItemData.put(field, new LinkedHashMap<>());
                            case List list -> targetItemData.put(field, new ArrayList<>());
                            case String string -> targetItemData.put(field, "");
                            case null, default -> targetItemData.put(field, null);
                        }
                    }
                }

                // Loop through all the source fields
                for (String field : sourceItemFields) {
                    // Check if the target has the field
                    if (targetItemFields.contains(field)) {
                        // Set the target field to the source field
                        targetItemData.put(field, sourceItemData.get(field));
                    }
                }

                // Set the target item metadata with the updated metadata
                apiClient.setItemMetadata(targetItemId, targetItemData);

                System.out.println("Successfully copied the metadata");

                // Migrate the images
                if (Boolean.parseBoolean(configData.getProperty("copyImages"))) {
                    System.out.println("Copying images.....");

                    // Get the list of images for the source and target items
                    ArrayList<HashMap<String, Object>> sourceImageList = apiClient.getItemImageList(sourceItemId);
                    ArrayList<HashMap<String, Object>> targetImageList = apiClient.getItemImageList(targetItemId);

                    // Loop through each target image
                    for (HashMap<String, Object> image : targetImageList) {
                        // Delete the image. Don't use the index because the index changes whenever an image is deleted so the last one will fail.
                        apiClient.deleteItemImage(targetItemId, (String) image.get("ImageType"));
                    }

                    // Loop through each source image
                    for (HashMap<String, Object> image : sourceImageList) {
                        // Download the image from the source item. Get the index to make sure we are downloading the correct one.
                        Integer imageIndex = Objects.requireNonNullElse((Integer) image.get("ImageIndex"), 0);

                        // Download the image from the source item
                        byte[] imageData = apiClient.getItemImage(sourceItemId, (String) image.get("ImageType"), imageIndex);

                        // Upload the image to the target item. Use the index otherwise the images will be added in reverse order because it will push the other ones back each time.
                        apiClient.setItemImage(targetItemId, (String) image.get("ImageType"), imageIndex, imageData);
                    }

                    System.out.println("Successfully copied the images");
                }
            }

            System.out.println("Copy successful!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
