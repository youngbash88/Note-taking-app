package com.mycompany.notetakingapp;

import java.util.ArrayList;
import java.util.List;

public class User {

    // Data variables to store user information
    private String username;
    private String passwordHash;
    private List<Note> notes;

    // Constructor to initialize user data
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.notes = new ArrayList<>(); // Initially, the user has no notes
    }

    // Method to create a new note for the user
    public Note createNote(String title, String content) {
        Note note = new Note(title, content);
        notes.add(note); // Adds the new note to the user's note list
        return note; // Returns the created note
    }

    // Method to get all the notes for the user
    public List<Note> getNotes() {
        return notes; // Returns the list of notes
    }

    // Method to get the username of the user
    public String getUsername() {
        return username; // Returns the user's username
    }

    
}
