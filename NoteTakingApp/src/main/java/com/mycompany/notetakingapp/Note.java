package com.mycompany.notetakingapp;

import java.io.Serializable;
import java.util.Objects;

public class Note implements Serializable {
    private String title;
    private String content;
    private String imagePath;
    private String sketchPath;

    public Note(String title, String content, String imagePath, String sketchPath) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.sketchPath = sketchPath;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getSketchPath() {
        return sketchPath;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setSketchPath(String sketchPath) {
        this.sketchPath = sketchPath;
    }

    // Override equals and hashCode to ensure proper comparison and functionality in lists
  @Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    Note note = (Note) obj;
    return title.equals(note.title) &&
           content.equals(note.content) &&
           Objects.equals(imagePath, note.imagePath) &&
           Objects.equals(sketchPath, note.sketchPath);
}

@Override
public int hashCode() {
    return Objects.hash(title, content, imagePath, sketchPath);
}


    
}
