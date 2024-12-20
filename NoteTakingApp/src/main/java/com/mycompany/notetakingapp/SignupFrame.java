package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignupFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signupButton;
    private JLabel statusLabel, usernameLabel, passwordLabel, back;
    

    public SignupFrame() {
        ImageIcon im = new ImageIcon("notes.png");
        setIconImage(im.getImage());
        setTitle("Sign up");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(80, 130, 180));
        setResizable(false);

        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        signupButton = new JButton("Sign up");
        statusLabel = new JLabel();
        back = new JLabel("Back");

        signupButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in both fields.");
                statusLabel.setForeground(new Color(199, 0, 57));
                return;
            }

            if (!username.matches("^[a-zA-Z0-9_]{4,20}$")) {
                statusLabel.setText("Username must be 4-20 alphanumeric characters.");
                statusLabel.setForeground(new Color(199, 0, 57));
                return;
            }
            if (password.length() < 6) {
                statusLabel.setText("Password must be at least 6 characters.");
                statusLabel.setForeground(new Color(199, 0, 57));
                return;
            }

            User newUser = new User (username,password);

            try {
                boolean success = FileManager.saveUserToFile(newUser);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Account Created Successfully");
                    new LoginFrame().setVisible(true);
                    dispose();
                } else {
                    statusLabel.setText("Username already exists.");
                    statusLabel.setForeground(new Color(199, 0, 57));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> signupButton.doClick());

        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        usernameLabel.setBounds(50, 50, 100, 30);
        usernameField.setBounds(150, 50, 200, 30);
        passwordLabel.setBounds(50, 100, 100, 30);
        passwordField.setBounds(150, 100, 200, 30);
        signupButton.setBounds(140, 150, 140, 30);
        statusLabel.setBounds(50, 190, 300, 30);
        back.setBounds(20, 10, 40, 30);
        signupButton.setBackground(Color.LIGHT_GRAY);

        back.setForeground(new Color(6, 49, 93));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(signupButton);
        add(statusLabel);
        add(back);
    }
}

