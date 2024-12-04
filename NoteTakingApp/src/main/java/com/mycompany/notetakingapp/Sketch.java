package com.mycompany.notetakingapp;

import java.io.File;
import java.io.IOException;

public class Sketch {
    // Data
    private String filePath;

    // Constructor
    public Sketch(String filePath) {
        this.filePath = filePath;
    }

    // Getters
    public String getFilePath() {
        return filePath;
    }

    // Method to save sketch to a new file
    public void saveSketch(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.createNewFile()) {
            throw new IOException("Couldn't create sketch file at path: " + filePath);
        }
        this.filePath = filePath;
    }
}
