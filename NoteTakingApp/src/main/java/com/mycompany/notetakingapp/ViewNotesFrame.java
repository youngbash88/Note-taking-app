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
    private JEditorPane noteContentArea; // Changed to JEditorPane for editing
    private JList<ImageIcon> imagesList; // Changed to a JList for images
    private DefaultListModel<ImageIcon> imagesListModel;
    private JButton editButton, deleteButton;

    public ViewNotesFrame(List<Note> notes, String username) {
        this.notes = notes;
        this.username = username;

        ImageIcon im = new ImageIcon("notes.png");
        setIconImage(im.getImage());
        setTitle(username);
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(80, 130, 180));
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

        // Note content editor and images list
        noteContentArea = new JEditorPane(); // Editable text area
        noteContentArea.setEditable(true);

        imagesListModel = new DefaultListModel<>();
        imagesList = new JList<>(imagesListModel);
        imagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imagesList.setVisibleRowCount(4); // Limit visible rows
        imagesList.setLayoutOrientation(JList.VERTICAL);

        JScrollPane imagesScrollPane = new JScrollPane(imagesList);
        imagesScrollPane.setBorder(BorderFactory.createTitledBorder("Attached Images"));

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

        // Right Panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.add(new JScrollPane(noteContentArea), BorderLayout.CENTER);
        rightPanel.add(imagesScrollPane, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Add panels to the frame
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void displayNoteContent(String selectedTitle) {
        for (Note note : notes) {
            if (note.getTitle().equals(selectedTitle)) {
                noteContentArea.setText(note.getContent());

                // Update images list
                imagesListModel.clear();
                if (note.getImagePaths() != null && !note.getImagePaths().isEmpty()) {
                    for (String imagePath : note.getImagePaths()) {
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            imagesListModel.addElement(new ImageIcon(imagePath));
                        }
                    }
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

        // Allow user to edit the note content directly
        String newContent = noteContentArea.getText();

        // Allow user to update images
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Add Image");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int imageResult = fileChooser.showOpenDialog(this);
        if (imageResult == JFileChooser.APPROVE_OPTION) {
            String newImagePath = fileChooser.getSelectedFile().getAbsolutePath();
            oldNote.addImagePath(newImagePath);
        }

        // Update the note in the file using FileManager
        boolean success = FileManager.updateNoteInFile(oldNote, oldNote, username);
        if (success) {
            // Refresh the display to show updated data
            displayNoteContent(selectedTitle);
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
                    imagesListModel.clear();

                    JOptionPane.showMessageDialog(this, "Note deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting note.");
                }
            }
        }
    }
}
