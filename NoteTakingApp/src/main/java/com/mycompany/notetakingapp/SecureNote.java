package com.mycompany.notetakingapp;

import java.io.Serializable;

public class SecureNote extends Note implements Serializable {
    private String password;

    // Constructor for SecureNote
    public SecureNote(String title, String content, String imagePath, String sketchPath, String password) {
        super(title, content, imagePath, sketchPath);  // Call the parent constructor (Note class)
        this.password = password;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
