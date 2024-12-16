package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewNotesFrame extends JFrame {

    private List<Note> notes;
    private String username;

    // Constructor accepting both List<Note> and String (username)
    public ViewNotesFrame(List<Note> notes, String username) {
        this.notes = notes;
        this.username = username;

        setTitle("View Notes");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add UI components here
        // For example, display notes in a JTextArea or JTable
    }
    
    // Method to display the notes (you can customize how you display them)
    public void displayNotes() {
        // Example: Print the titles of the notes in a JTextArea
        JTextArea textArea = new JTextArea(20, 30);
        textArea.setEditable(false);
        for (Note note : notes) {
            textArea.append(note.getTitle() + "\n");
        }
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }
}
