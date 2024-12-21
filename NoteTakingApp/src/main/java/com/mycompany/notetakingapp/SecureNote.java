package com.mycompany.notetakingapp;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class SecureNote extends Note implements Serializable {
    private String password;
    
    // Constructor for SecureNote with single image path
    public SecureNote(String title, String content, String imagePath, String sketchPath, String password) {
        super(title, content, imagePath, sketchPath);  // Call the parent constructor with single image path
        this.password = password;
    }
    
    // Constructor for SecureNote with List of image paths
    public SecureNote(String title, String content, List<String> imagePaths, String sketchPath, String password) {
        super(title, content, imagePaths, sketchPath);  // Call the parent constructor with List
        this.password = password;
    }
    
    // Getter for password
    public String getPassword() {
        return password;
    }
    
    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same reference
        if (!(obj instanceof SecureNote)) return false; // Different class type
        if (!super.equals(obj)) return false; // Check equality of parent class fields

        SecureNote other = (SecureNote) obj;
        return Objects.equals(this.password, other.password); // Compare passwords
    }
}