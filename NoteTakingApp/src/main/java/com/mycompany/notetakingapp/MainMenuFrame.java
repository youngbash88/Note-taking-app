package com.mycompany.notetakingapp;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuFrame extends JFrame {
    private JButton viewNotesButton, createNoteButton, createSecureNoteButton, logoutButton;
    private String username;

    public MainMenuFrame(String username) {
        this.username = username;
        setTitle("Main Menu");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        viewNotesButton = new JButton("View Notes");
        createNoteButton = new JButton("Create Note");
        createSecureNoteButton = new JButton("Create Secure Note");
        logoutButton = new JButton("Logout");

        viewNotesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Note> notes = FileManager.loadNotesFromFile(username); 
                new ViewNotesFrame(notes, username).setVisible(true);  
                dispose();
            }
        });

        createNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateNoteFrame(username).setVisible(true);
                dispose();
            }
        });

        createSecureNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateSecureNoteFrame(username).setVisible(true);
                dispose();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        setLayout(new FlowLayout());
        add(viewNotesButton);
        add(createNoteButton);
        add(createSecureNoteButton);
        add(logoutButton);
    }
}
