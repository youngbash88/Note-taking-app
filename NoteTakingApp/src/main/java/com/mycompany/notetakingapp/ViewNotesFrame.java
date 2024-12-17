package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewNotesFrame extends JFrame {
    private List<Note> notes;
    private String username;
    private JList<String> notesList;
    private DefaultListModel<String> notesListModel;
    private JTextArea noteContentArea;
    private JLabel imageLabel, sketchLabel;

    public ViewNotesFrame(List<Note> notes, String username) {
        this.notes = notes;
        this.username = username;

        setTitle("View Notes - " + username);
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Notes list
        notesListModel = new DefaultListModel<>();
        notesList = new JList<>(notesListModel);
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        for (Note note : notes) {
            notesListModel.addElement(note.getTitle());
        }
        notesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displayNoteContent(notesList.getSelectedValue());
            }
        });
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JScrollPane(notesList), BorderLayout.CENTER);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Note content, image, and sketch
        noteContentArea = new JTextArea(10, 40);
        noteContentArea.setEditable(false);
        noteContentArea.setLineWrap(true);
        noteContentArea.setWrapStyleWord(true);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        sketchLabel = new JLabel();
        sketchLabel.setHorizontalAlignment(JLabel.CENTER);
        sketchLabel.setVerticalAlignment(JLabel.CENTER);
        sketchLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel rightPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        rightPanel.add(new JScrollPane(noteContentArea));
        rightPanel.add(new JScrollPane(imageLabel));
        rightPanel.add(new JScrollPane(sketchLabel));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel), BorderLayout.CENTER);
    }

    private void displayNoteContent(String selectedTitle) {
        for (Note note : notes) {
            if (note.getTitle().equals(selectedTitle)) {
                noteContentArea.setText(note.getContent());

                if (note.getImagePath() != null && !note.getImagePath().isEmpty()) {
                    imageLabel.setIcon(new ImageIcon(note.getImagePath()));
                    imageLabel.setText("");
                } else {
                    imageLabel.setIcon(null);
                    imageLabel.setText("No image available.");
                }

                if (note.getSketchPath() != null && !note.getSketchPath().isEmpty()) {
                    sketchLabel.setIcon(new ImageIcon(note.getSketchPath()));
                    sketchLabel.setText("");
                } else {
                    sketchLabel.setIcon(null);
                    sketchLabel.setText("No sketch available.");
                }
                break;
            }
        }
    }
}