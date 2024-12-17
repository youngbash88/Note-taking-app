package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class CreateNoteFrame extends JFrame {
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton, imageButton, sketchButton;
    private String imagePath = null, sketchPath = null;

    public CreateNoteFrame(String username) {
        setTitle("Create Note");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize UI elements
        titleField = new JTextField(20);
        contentArea = new JTextArea(5, 20);
        saveButton = new JButton("Save Note");
        imageButton = new JButton("Add Image");
        sketchButton = new JButton("Add Sketch");

        // Action listener for Save Button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String content = contentArea.getText();

                // Check if title and content are empty
                if (title.isEmpty() || content.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill out the title and content.");
                    return;
                }

                // Save the note with image and sketch paths
                Note note = new Note(title, content, imagePath, sketchPath);

                // Ensure that at least one path is not null or empty before saving
                if (imagePath != null || sketchPath != null) {
                    FileManager.saveNoteToFile(note, username);
                    JOptionPane.showMessageDialog(null, "Note saved successfully");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Please add an image or a sketch.");
                }
            }
        });

        // Action listener for the "Add Image" button
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Image");
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePath = selectedFile.getAbsolutePath();
                }
            }
        });

        // Action listener for the "Add Sketch" button
        sketchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Sketch File");
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    sketchPath = selectedFile.getAbsolutePath();
                }
            }
        });

        // Layout and adding components to the frame
        setLayout(new FlowLayout());
        add(new JLabel("Title:"));
        add(titleField);
        add(new JLabel("Content:"));
        add(new JScrollPane(contentArea));
        add(imageButton);
        add(sketchButton);
        add(saveButton);
    }
}
