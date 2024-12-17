package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreateAccountFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton createAccountButton;
    private JLabel statusLabel;

    public CreateAccountFrame() {
        setTitle("Create New Account");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create input fields and buttons
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        createAccountButton = new JButton("Create Account");
        statusLabel = new JLabel();

        // Create Account Button Action
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validate inputs
                if (username.isEmpty() || password.isEmpty()) {
                    statusLabel.setText("Please fill in both fields.");
                    statusLabel.setForeground(Color.RED);
                    return;
                }

                // Hash the password before saving (you can use PasswordHash class)
                String hashedPassword = PasswordHash.hashPassword(password);

                // Create a new user and try to save it
                User newUser = new User(username, hashedPassword);
                boolean success = FileManager.saveUserToFile(newUser);

                if (success) {
                    JOptionPane.showMessageDialog(null, "Account Created Successfully");
                    dispose();  // Close the create account frame
                } else {
                    JOptionPane.showMessageDialog(null, "Username already exists");
                }
            }
        });

        // Layout setup
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(20)); // Add space at the top
        add(new JLabel("Username:"));
        add(usernameField);
        add(Box.createVerticalStrut(10)); // Add space between fields
        add(new JLabel("Password:"));
        add(passwordField);
        add(Box.createVerticalStrut(20)); // Add space between password and buttons
        add(createAccountButton);
        add(Box.createVerticalStrut(10)); // Add space between button and status label
        add(statusLabel);

        // Adjust frame size to fit content
        pack();
    }

    public static void main(String[] args) {
        new CreateAccountFrame().setVisible(true);  // Launch the frame
    }
}
