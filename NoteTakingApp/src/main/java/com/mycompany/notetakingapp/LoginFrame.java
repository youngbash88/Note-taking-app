package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, createAccountButton;

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        createAccountButton = new JButton("Create New Account");

        // Login Button Action
        loginButton.addActionListener(this::handleLogin);

        // Create Account Button Action
        createAccountButton.addActionListener(this::handleCreateAccount);

        // Layout setup
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));  // Vertical layout
        add(Box.createVerticalStrut(20));  // Add space at the top
        add(new JLabel("Username:"));
        add(usernameField);
        add(Box.createVerticalStrut(10));  // Space between fields
        add(new JLabel("Password:"));
        add(passwordField);
        add(Box.createVerticalStrut(20));  // Space between fields and buttons
        add(loginButton);
        add(createAccountButton);
        add(Box.createVerticalStrut(20));  // Space at the bottom

        pack();  // Adjust frame size to fit content
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validate login credentials using the map of users and password hashing
        Map<String, String> users = FileManager.loadUsersFromFile();
        String hashedPassword = PasswordHash.hashPassword(password);

        // Check if the username exists and the hashed password matches
        if (users.containsKey(username) && users.get(username).equals(hashedPassword)) {
            new MainMenuFrame(username).setVisible(true);  // Open the main menu
            dispose();  // Close the login frame
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void handleCreateAccount(ActionEvent e) {
        new CreateAccountFrame().setVisible(true);  // Open the create account frame
        dispose();  // Close the login frame
    }
    
}
