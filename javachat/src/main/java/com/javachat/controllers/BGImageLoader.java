package com.javachat.controllers;

import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

/**
 * Utility class for loading images from resource paths and creating BackgroundImages for JavaFX applications.
 */
public class BGImageLoader {

    /**
     * Loads an image from a specified resource path and creates a BackgroundImage.
     * The image is not repeated and is centered with specified size properties.
     *
     * @param resourcePath The path to the image resource within the classpath.
     * @return A BackgroundImage object if the image is loaded successfully; null if there is an error.
     */
    public static BackgroundImage load(String resourcePath) {
        // Load the image from the specified resource path
        Image image = new Image(BGImageLoader.class.getResourceAsStream(resourcePath));

        // Check for loading errors
        if (image.isError()) {
            System.out.println("Image loading failed: " + image.getException());
            return null;
        }

        // Create and return a new BackgroundImage object with specific properties
        BackgroundImage bgImage = new BackgroundImage(
            image,  // The image
            BackgroundRepeat.NO_REPEAT, // Do not repeat the image
            BackgroundRepeat.NO_REPEAT, // Do not repeat the image
            BackgroundPosition.CENTER,  // Center the image
            new BackgroundSize(
                100, 100, // Size of the image (width, height)
                true, true, // Percentage-based sizing
                false, true) // Cover the region without preserving the aspect ratio
        );

        return bgImage;
    }
}
