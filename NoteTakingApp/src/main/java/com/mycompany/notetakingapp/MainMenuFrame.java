package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainMenuFrame extends JFrame {
    private JButton viewNotesButton, createNoteButton, createSecureNoteButton, logoutButton;
    private String username;
    private JLabel welcomeLabel;

    public MainMenuFrame(String username) {
        this.username = username;

        // Frame setup
        setTitle("Main Menu");
        setSize(420, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(new Color(80, 130, 180));
        setIconImage(new ImageIcon("notes.png").getImage());

        // Initialize UI components
        initializeComponents();

        // Add components to the frame
        addComponents();
    }

    private void initializeComponents() {
        // Welcome Label
        welcomeLabel = new JLabel("Welcome ☺");
        welcomeLabel.setBounds(130, 20, 200, 40);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));

        // View Notes Button
        viewNotesButton = createButton("View Notes", 80, e -> {
            List<Note> notes = FileManager.loadNotesFromFile(username);
            new ViewNotesFrame(notes, username).setVisible(true);
            dispose();
        });

        // Create Note Button
        createNoteButton = createButton("New Note", 130, e -> {
            List<Note> emptyNotes = new ArrayList<>();
            new ViewNotesFrame(emptyNotes, username).setVisible(true);
            dispose();
        });

        // Create Secure Note Button
        createSecureNoteButton = createButton("New Secure Note", 180, e -> {
            createSecureNote();
        });

        // Logout Button
        logoutButton = createButton("Logout", 230, e -> confirmLogout());
        logoutButton.setBackground(new Color(199, 0, 57));
    }

    private JButton createButton(String text, int yPosition, ActionListener action) {
        JButton button = new JButton(text);
        button.setBounds(110, yPosition, 200, 30);
        button.setBackground(Color.LIGHT_GRAY);
        button.addActionListener(action);
        return button;
    }

    private void addComponents() {
        add(welcomeLabel);
        add(viewNotesButton);
        add(createNoteButton);
        add(createSecureNoteButton);
        add(logoutButton);
    }

    private void confirmLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    private void createSecureNote() {
        // Prompt for secure note details
        String title = JOptionPane.showInputDialog(this, "Enter Secure Note Title:");
        if (title == null || title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required!");
            return;
        }

        String content = JOptionPane.showInputDialog(this, "Enter Secure Note Content:");
        if (content == null || content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Content is required!");
            return;
        }

        JPasswordField passwordField = new JPasswordField();
        int passwordOption = JOptionPane.showConfirmDialog(this, passwordField, "Enter Password for Secure Note", JOptionPane.OK_CANCEL_OPTION);

        if (passwordOption == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty!");
                return;
            }

            // Create and save secure note
            SecureNote secureNote = new SecureNote(title, content, new ArrayList<>(), username, password);
            boolean success = FileManager.saveNewNoteToFile(secureNote, username);
            if (success) {
                JOptionPane.showMessageDialog(this, "Secure Note created successfully!");
                // Open ViewNotesFrame after creating the note
                List<Note> notes = FileManager.loadNotesFromFile(username);
                new ViewNotesFrame(notes, username).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error saving secure note.");
            }
        }
    }

    
}
