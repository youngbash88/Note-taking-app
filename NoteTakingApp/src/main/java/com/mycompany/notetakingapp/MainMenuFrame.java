package com.mycompany.notetakingapp;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuFrame extends JFrame {
    private JButton viewNotesButton, createNoteButton, createSecureNoteButton, logoutButton;
    private String username;
    private JLabel wellcome;
    public MainMenuFrame(String username) {
        this.username = username;
        ImageIcon im = new ImageIcon("notes.png");
        setIconImage(im.getImage());
        setTitle("Main Menu");
        setSize(420, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(80, 130, 180));
        setResizable(false);
        
        wellcome = new JLabel ("Wellcome ☺");
        viewNotesButton = new JButton("View Notes");
        createNoteButton = new JButton("Create Note");
        createSecureNoteButton = new JButton("Create Secure Note");
        logoutButton = new JButton("Logout");
        
        
        viewNotesButton.setBounds(110, 80, 200, 30);
        createNoteButton.setBounds(110, 130, 200, 30);
        createSecureNoteButton.setBounds(110, 180, 200, 30);
        logoutButton.setBounds(110, 230, 200, 30);
        wellcome.setBounds(130, 20, 200, 40);
        wellcome.setFont(new java.awt.Font("Times New Roman", 1, 30));
        viewNotesButton.setBackground(Color.LIGHT_GRAY);
        createNoteButton.setBackground(Color.LIGHT_GRAY);
        createSecureNoteButton.setBackground(Color.LIGHT_GRAY);
        logoutButton.setBackground(new Color(199, 0, 57));


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

        add(viewNotesButton);
        add(createNoteButton);
        add(createSecureNoteButton);
        add(logoutButton);
        add(wellcome);
    }
}
