package com.mycompany.notetakingapp;

import java.io.File;

public class Image {
    // Data
    private String filePath;
    private String description;
    
    // Constructor
    public Image(String filePath, String description) {
        this.filePath = filePath;
        this.description = description;
    }
    
    // Getters
    public String getFilePath() {
        return filePath;
    }

    public String getDescription() {
        return description;
    }
    
    // Method to load image from a given file path
    public void loadImage(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Image file not found: " + filePath);
        }
        this.filePath = filePath;
    }
    
   
}
