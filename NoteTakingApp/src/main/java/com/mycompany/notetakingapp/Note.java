package com.mycompany.notetakingapp;

import java.util.ArrayList;
import java.util.List;

public class Note {
    // Data
    private String title;
    private String content;
    private List<Image> images;
    private List<Sketch> sketches;

    // Constructor
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.images = new ArrayList<>();
        this.sketches = new ArrayList<>();
    }

    // Add new image
    public void addImage(Image image) {
        images.add(image);
    }

    // Add new sketch
    public void addSketch(Sketch sketch) {
        sketches.add(sketch);
    }

    // Update content
    public void updateContent(String content) {
        this.content = content;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<Image> getImages() {
        return images;
    }

    public List<Sketch> getSketches() {
        return sketches;
    }

    // Remove image
    public void removeImage(Image image) {
        images.remove(image);
    }

    // Remove sketch
    public void removeSketch(Sketch sketch) {
        sketches.remove(sketch);
    }

  
}
