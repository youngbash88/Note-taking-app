package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditNoteFrame extends JFrame {
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private Note note;
    private String username;

    public EditNoteFrame(Note note, String username) {
        this.note = note;
        this.username = username;

        setTitle("Edit Note");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize UI elements
        titleField = new JTextField(note.getTitle(), 20);
        contentArea = new JTextArea(note.getContent(), 5, 20);
        saveButton = new JButton("Save Changes");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String content = contentArea.getText();

                // Update the note and save it
                Note updatedNote = new Note(title, content, note.getImagePath(), note.getSketchPath());
                FileManager.updateNoteInFile(note, updatedNote, username);
                JOptionPane.showMessageDialog(null, "Note updated successfully");
                dispose();
            }
        });

        // Layout and adding components
        setLayout(new FlowLayout());
        add(new JLabel("Title:"));
        add(titleField);
        add(new JLabel("Content:"));
        add(new JScrollPane(contentArea));
        add(saveButton);
    }
}
