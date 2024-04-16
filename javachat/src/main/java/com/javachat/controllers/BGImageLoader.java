package com.javachat.controllers;

import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

public class BGImageLoader {
  public static BackgroundImage load(String resourcePath) {
    // Load the image
    Image image = new Image(BGImageLoader.class.getResourceAsStream(resourcePath));
    if (image.isError()) {
      System.out.println("Image loading failed: " + image.getException());
      return null;
    }
    // Create a BackgroundImage
    BackgroundImage bgImage = new BackgroundImage(image,
        BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER,
        new BackgroundSize(100, 100, true, true, false, true));

    return bgImage;

  }
}
