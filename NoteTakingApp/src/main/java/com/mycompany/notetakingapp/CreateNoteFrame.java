package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class CreateNoteFrame extends JFrame {
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton, imageButton, sketchButton;
    private String imagePath, sketchPath;

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

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String content = contentArea.getText();

                // Save the note with image and sketch paths
                Note note = new Note(title, content, imagePath, sketchPath);
                FileManager.saveNoteToFile(note, username);
                JOptionPane.showMessageDialog(null, "Note saved successfully");
                dispose();
            }
        });

        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePath = selectedFile.getAbsolutePath();
                }
            }
        });

        sketchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Assuming Sketch is another class with its own handling
                sketchPath = "sketchFilePath"; // Replace with actual path from Sketch component
            }
        });

        // Layout and adding components
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
