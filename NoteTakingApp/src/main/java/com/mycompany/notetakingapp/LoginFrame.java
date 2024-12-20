package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel usernameLabel, passwordLabel, login;
    private JButton loginButton, signupButton;

    public LoginFrame() {
        ImageIcon im = new ImageIcon("notes.png");

        setIconImage(im.getImage());
        setTitle("Login");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(80, 130, 180));
        setResizable(false);

        // Initialize components
        login = new JLabel("Please Login!");
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign up");
        
        
        loginButton.setBackground(Color.lightGray);

        signupButton.setBackground(Color.lightGray);

        // Set bounds for components
        usernameLabel.setBounds(50, 60, 200, 30); // x, y, width, height
        usernameField.setBounds(150, 60, 200, 30);
        passwordLabel.setBounds(50, 110, 100, 30);
        passwordField.setBounds(150, 110, 200, 30);
        loginButton.setBounds(50, 160, 140, 30);
        signupButton.setBounds(210, 160, 140, 30);
        login.setBounds(130, 10, 200, 40);
        login.setFont(new java.awt.Font("Times New Roman", 1, 30));

        // Action for pressing Enter in the username field
        usernameField.addActionListener(e -> passwordField.requestFocus());

        // Action for pressing Enter in the password field
        passwordField.addActionListener(e -> loginButton.doClick());

        // Login Button Action
        loginButton.addActionListener(this::handleLogin);

        // Create Account Button Action
        signupButton.addActionListener(this::handleCreateAccount);
        // Add components to the frame
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signupButton);
        add(usernameLabel);
        add(usernameField);
        add(login);

    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim().toLowerCase();  // Normalize username
        String password = new String(passwordField.getPassword());

        // Validate login credentials
        Map<String, String> users = FileManager.loadUsersFromFile();
        String hashedPassword = PasswordHash.hashPassword(password);

        // Debugging logs
        System.out.println("Entered Username: " + username);
        System.out.println("Entered Password: " + password);
        System.out.println("Loaded Users: " + users);
        System.out.println("Hashed Password: " + hashedPassword);
        
        System.out.println("Password entered during login: " + password);
        System.out.println("Hashed during login: " + PasswordHash.hashPassword(password));


        // Check if the username exists and the hashed password matches
        if (users.containsKey(username) && users.get(username).equals(hashedPassword)) {
            new MainMenuFrame(username).setVisible(true);  // Open the main menu
            dispose();  // Close the login frame
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateAccount(ActionEvent e) {
        new SignupFrame().setVisible(true);  // Open the create account frame
        dispose();  // Close the login frame
    }

}
