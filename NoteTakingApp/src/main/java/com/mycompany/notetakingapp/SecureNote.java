package com.mycompany.notetakingapp;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

public class SecureNote extends Note implements Serializable {
    private String password;
    
    public SecureNote(String title, String content, String password) {
        super(title, content);
        this.password = password;
    }
    
    public SecureNote(String title, String content, List<String> imagePaths, 
                     List<String> sketchPaths, String password) {
        super(title, content, imagePaths, sketchPaths);
        this.password = password;
    }
    
    // Add this constructor to match the parameters
    public SecureNote(String title, String content, List<String> imagePaths, String username, String password) {
        super(title, content, imagePaths, new ArrayList<>());
        this.password = password;
    }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof SecureNote)) return false;
        SecureNote other = (SecureNote) obj;
        return Objects.equals(password, other.password);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), password);
    }
}