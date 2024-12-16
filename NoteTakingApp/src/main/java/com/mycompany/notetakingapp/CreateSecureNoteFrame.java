package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class CreateSecureNoteFrame extends JFrame {

    private String username;
    private JTextField titleField;
    private JTextArea contentArea;
    private JPasswordField passwordField;
    private String imagePath;
    private String sketchPath;

    // Constructor accepting username
    public CreateSecureNoteFrame(String username) {
        this.username = username;

        setTitle("Create Secure Note");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up the UI components for creating a secure note
        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField(20);

        JLabel contentLabel = new JLabel("Content:");
        contentArea = new JTextArea(5, 20);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JButton saveButton = new JButton("Save Secure Note");

        // Add action listener for saving secure note
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSecureNote();
            }
        });

        // Buttons to select image and sketch
        JButton selectImageButton = new JButton("Select Image");
        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImage();
            }
        });

        JButton selectSketchButton = new JButton("Select Sketch");
        selectSketchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectSketch();
            }
        });

        // Layout setup
        setLayout(new FlowLayout());
        add(titleLabel);
        add(titleField);
        add(contentLabel);
        add(contentScrollPane);
        add(passwordLabel);
        add(passwordField);
        add(selectImageButton);
        add(selectSketchButton);
        add(saveButton);
    }

    // Method to select an image using a file chooser
    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePath = selectedFile.getAbsolutePath();
        }
    }

    // Method to select a sketch using a file chooser
    private void selectSketch() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Sketch");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            sketchPath = selectedFile.getAbsolutePath();
        }
    }

    // Method to save the secure note
    private void saveSecureNote() {
        String title = titleField.getText();
        String content = contentArea.getText();
        char[] password = passwordField.getPassword();
        String passwordStr = new String(password);

        // Check if any required field is empty
        if (title.isEmpty() || content.isEmpty() || passwordStr.isEmpty() || imagePath == null || sketchPath == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select image/sketch.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create and save the secure note
        SecureNote secureNote = new SecureNote(title, content, imagePath, sketchPath, passwordStr);

        // Save the secure note using FileManager (you can modify FileManager to handle SecureNote saving)
        FileManager.saveNoteToFile(secureNote, username);

        // Show a success message
        JOptionPane.showMessageDialog(this, "Secure note saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Close the current frame
        dispose();
    }
}
