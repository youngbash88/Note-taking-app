package com.mycompany.notetakingapp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Note implements Serializable {
    private String title;
    private String content;
    protected List<String> imagePaths; // Changed to protected for inheritance
    private String sketchPath;
    
    public Note(String title, String content, String sketchPath) {
        this.title = title;
        this.content = content;
        this.imagePaths = new ArrayList<>();
        this.sketchPath = sketchPath;
    }
    
    public Note(String title, String content, List<String> imagePaths, String sketchPath) {
        this.title = title;
        this.content = content;
        this.imagePaths = imagePaths != null ? new ArrayList<>(imagePaths) : new ArrayList<>();
        this.sketchPath = sketchPath;
    }
    
    // Constructor for single image path
    public Note(String title, String content, String imagePath, String sketchPath) {
        this.title = title;
        this.content = content;
        this.imagePaths = new ArrayList<>();
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            this.imagePaths.add(imagePath);
        }
        this.sketchPath = sketchPath;
    }
    
    // Existing getters
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getImagePaths() { return new ArrayList<>(imagePaths); }
    public String getSketchPath() { return sketchPath; }
    
    // Existing setters
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setSketchPath(String sketchPath) { this.sketchPath = sketchPath; }
    
    // Updated setImagePath methods
    public void setImagePath(List<String> paths) {
        this.imagePaths = paths != null ? new ArrayList<>(paths) : new ArrayList<>();
    }
    
    public void setImagePath(String path) {
        this.imagePaths = new ArrayList<>();
        if (path != null && !path.trim().isEmpty()) {
            this.imagePaths.add(path);
        }
    }
    
    // Existing methods
    public void addImagePath(String imagePath) {
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            this.imagePaths.add(imagePath);
        }
    }
    
    public void removeImagePath(String imagePath) {
        this.imagePaths.remove(imagePath);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Note note = (Note) obj;
        return title.equals(note.title) &&
               content.equals(note.content) &&
               Objects.equals(imagePaths, note.imagePaths) &&
               Objects.equals(sketchPath, note.sketchPath);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(title, content, imagePaths, sketchPath);
    }
}