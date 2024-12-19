package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ViewNotesFrame extends JFrame {
    private List<Note> notes;
    private String username;
    private JList<String> notesList;
    private DefaultListModel<String> notesListModel;
    private JTextArea noteContentArea;
    private JLabel imageLabel, sketchLabel;
    private JButton editButton, deleteButton;  // Added delete button

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

        // Edit Button
        editButton = new JButton("Edit Note");
        editButton.addActionListener(e -> {
            editNote();
        });

        // Delete Button
        deleteButton = new JButton("Delete Note");
        deleteButton.addActionListener(e -> {
            deleteNote();
        });

        // Right Panel with 50/50 split for Edit and Delete buttons
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 10, 10));  // Split into 2 rows
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));  // Content and images side by side
        contentPanel.add(new JScrollPane(noteContentArea));
        contentPanel.add(new JScrollPane(imageLabel));
        contentPanel.add(new JScrollPane(sketchLabel));

        rightPanel.add(contentPanel);  // First half for content
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));  // Buttons in separate columns
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        rightPanel.add(buttonPanel);  // Second half for buttons

        // Add panels to the frame
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

    private void editNote() {
        // Get the selected note
        String selectedTitle = notesList.getSelectedValue();
        if (selectedTitle == null) {
            JOptionPane.showMessageDialog(this, "Please select a note to edit.");
            return;
        }

        Note oldNote = null;
        for (Note note : notes) {
            if (note.getTitle().equals(selectedTitle)) {
                oldNote = note;
                break;
            }
        }

        // Show a dialog for editing the note's title and content
        String newTitle = JOptionPane.showInputDialog(this, "Edit Title:", oldNote.getTitle());
        if (newTitle == null || newTitle.isEmpty()) return;  // If user cancels or enters nothing, do nothing

        String newContent = JOptionPane.showInputDialog(this, "Edit Content:", oldNote.getContent());
        if (newContent == null) return;  // If user cancels, do nothing

        // Allow user to choose a new image and sketch
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        String newImagePath = oldNote.getImagePath();
        String newSketchPath = oldNote.getSketchPath();

        // Show file chooser for image
        int imageResult = fileChooser.showOpenDialog(this);
        if (imageResult == JFileChooser.APPROVE_OPTION) {
            newImagePath = fileChooser.getSelectedFile().getAbsolutePath();
        }

        // Show file chooser for sketch
        fileChooser.setDialogTitle("Select Sketch");
        int sketchResult = fileChooser.showOpenDialog(this);
        if (sketchResult == JFileChooser.APPROVE_OPTION) {
            newSketchPath = fileChooser.getSelectedFile().getAbsolutePath();
        }

        // Create an updated Note with the new data
        Note updatedNote = new Note(newTitle, newContent, newImagePath, newSketchPath);

        // Update the note in the file using FileManager
        boolean success = FileManager.updateNoteInFile(oldNote, updatedNote, username);
        if (success) {
            // Update the local list and UI
            oldNote.setTitle(newTitle);
            oldNote.setContent(newContent);
            oldNote.setImagePath(newImagePath);
            oldNote.setSketchPath(newSketchPath);

            // Refresh the display to show updated data
            displayNoteContent(newTitle);

            // Update the notes list UI with the new title
            notesListModel.setElementAt(newTitle, notesList.getSelectedIndex());

            JOptionPane.showMessageDialog(this, "Note updated successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Error updating note.");
        }
    }

    private void deleteNote() {
        // Get the selected note
        String selectedTitle = notesList.getSelectedValue();
        if (selectedTitle == null) {
            JOptionPane.showMessageDialog(this, "Please select a note to delete.");
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this note?", "Delete Note", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Remove note from the list and from the file
            Note noteToDelete = null;
            for (Note note : notes) {
                if (note.getTitle().equals(selectedTitle)) {
                    noteToDelete = note;
                    break;
                }
            }

            if (noteToDelete != null) {
                boolean success = FileManager.deleteNoteFromFile(noteToDelete, username);
                if (success) {
                    // Remove note from the list and refresh UI
                    notes.remove(noteToDelete);
                    notesListModel.removeElement(selectedTitle);
                    noteContentArea.setText("");
                    imageLabel.setIcon(null);
                    sketchLabel.setIcon(null);

                    JOptionPane.showMessageDialog(this, "Note deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting note.");
                }
            }
        }
    }
}
