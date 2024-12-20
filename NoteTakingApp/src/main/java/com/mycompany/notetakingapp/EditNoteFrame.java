package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import java.io.File;

public class EditNoteFrame extends JFrame {
    private JTextField titleField;
    private JTextArea contentArea;
    private JLabel imageLabel;
    private JButton imageButton;
    private JLabel sketchLabel;
    private JButton sketchButton;
    private JButton saveButton;

    private Note note; // The note to be edited
    private String username; // User to whom the note belongs
    private File selectedImage;
    private File selectedSketch;

    public EditNoteFrame(Note note, String username) {
        this.note = note;
        this.username = username;

        setTitle("Edit Note");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize UI elements with existing note details
        titleField = new JTextField(note.getTitle(), 20);
        contentArea = new JTextArea(note.getContent(), 5, 20);
        imageLabel = new JLabel("Current Image: " + (note.getImagePaths() != null ? note.getImagePaths() : "None"));
        imageButton = new JButton("Change Image");
        sketchLabel = new JLabel("Current Sketch: " + (note.getSketchPath() != null ? note.getSketchPath() : "None"));
        sketchButton = new JButton("Change Sketch");
        saveButton = new JButton("Save Changes");

        // Image selection button action
        imageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImage = fileChooser.getSelectedFile();
                imageLabel.setText("Selected Image: " + selectedImage.getName());
            }
        });

        // Sketch selection button action
        sketchButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedSketch = fileChooser.getSelectedFile();
                sketchLabel.setText("Selected Sketch: " + selectedSketch.getName());
            }
        });

        // Save button action
        saveButton.addActionListener(e -> {
            String newTitle = titleField.getText();
            String newContent = contentArea.getText();

            // Validate inputs
            if (newTitle.isEmpty() || newContent.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and content cannot be empty.");
                return;
            }

            // Copy old image and sketch paths if not updated
            var updatedImagePath = selectedImage != null ? selectedImage.getAbsolutePath() : note.getImagePaths();
            String updatedSketchPath = selectedSketch != null ? selectedSketch.getAbsolutePath() : note.getSketchPath();

            // Create a new updated note
            Note updatedNote = new Note(newTitle, newContent, (List<String>) updatedImagePath, updatedSketchPath);

            // Save the updated note
            boolean success = FileManager.updateNoteInFile(note, updatedNote, username);
            if (success) {
                JOptionPane.showMessageDialog(this, "Note updated successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update the note.");
            }
        });

        // Layout setup
        setLayout(new GridLayout(6, 1, 5, 5));
        add(new JLabel("Title:"));
        add(titleField);
        add(new JLabel("Content:"));
        add(new JScrollPane(contentArea));
        add(imageLabel);
        add(imageButton);
        add(sketchLabel);
        add(sketchButton);
        add(saveButton);
    }
}
