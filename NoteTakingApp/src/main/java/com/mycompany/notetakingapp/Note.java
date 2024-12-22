package com.mycompany.notetakingapp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Note implements Serializable {
    private String title;
    private String content;
    protected List<String> imagePaths;
    protected List<String> sketchPaths;
    
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.imagePaths = new ArrayList<>();
        this.sketchPaths = new ArrayList<>();
    }
    
    public Note(String title, String content, List<String> imagePaths, List<String> sketchPaths) {
        this.title = title;
        this.content = content;
        this.imagePaths = imagePaths != null ? new ArrayList<>(imagePaths) : new ArrayList<>();
        this.sketchPaths = sketchPaths != null ? new ArrayList<>(sketchPaths) : new ArrayList<>();
    }
    
    // Getters
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getImagePaths() { return new ArrayList<>(imagePaths); }
    public List<String> getSketchPaths() { return new ArrayList<>(sketchPaths); }
    
    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    
    // Media management methods
    public void addImagePath(String imagePath) {
        if (imagePath != null && !imagePath.trim().isEmpty() && !imagePaths.contains(imagePath)) {
            this.imagePaths.add(imagePath);
        }
    }
    
    public void addSketchPath(String sketchPath) {
        if (sketchPath != null && !sketchPath.trim().isEmpty() && !sketchPaths.contains(sketchPath)) {
            this.sketchPaths.add(sketchPath);
        }
    }
    
    public void removeImagePath(String imagePath) {
        this.imagePaths.remove(imagePath);
    }
    
    public void removeSketchPath(String sketchPath) {
        this.sketchPaths.remove(sketchPath);
    }
    
    // Add these methods for backward compatibility
    @Deprecated
    public String getSketchPath() {
        return sketchPaths.isEmpty() ? null : sketchPaths.get(0);
    }
    
    @Deprecated
    public void setSketchPath(String path) {
        sketchPaths.clear();
        if (path != null && !path.trim().isEmpty()) {
            sketchPaths.add(path);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Note note = (Note) obj;
        return title.equals(note.title) &&
               content.equals(note.content) &&
               Objects.equals(imagePaths, note.imagePaths) &&
               Objects.equals(sketchPaths, note.sketchPaths);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(title, content, imagePaths, sketchPaths);
    }
}