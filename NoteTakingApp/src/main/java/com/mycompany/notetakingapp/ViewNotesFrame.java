package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewNotesFrame extends JFrame {
    private List<Note> notes;
    private String username;
    private JList<String> notesList;
    private DefaultListModel<String> notesListModel;
    private JTextField noteTitleField;
    private JEditorPane noteContentArea;
    private JList<ImageIcon> imagesList;
    private DefaultListModel<ImageIcon> imagesListModel;
    private JList<ImageIcon> sketchesList;
    private DefaultListModel<ImageIcon> sketchesListModel;
    private JButton editButton, deleteButton, saveButton, backButton, sketchButton, addImageButton;
    
    // Constants for thumbnail size
    private static final int THUMBNAIL_WIDTH = 100;
    private static final int THUMBNAIL_HEIGHT = 100;

    public ViewNotesFrame(List<Note> notes, String username) {
        this.notes = notes;
        this.username = username;

        setupMainFrame();
        setupComponents();
        setupLayout();
        setupListeners();
    }

    private void setupMainFrame() {
        ImageIcon im = new ImageIcon("notes.png");
        setIconImage(im.getImage());
        setTitle(username);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(80, 130, 180));
        setLocationRelativeTo(null);
    }

    private void setupComponents() {
        // Notes List
        notesListModel = new DefaultListModel<>();
        notesList = new JList<>(notesListModel);
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        for (Note note : notes) {
            notesListModel.addElement(note.getTitle());
        }

        // Title Field
        noteTitleField = new JTextField();
        noteTitleField.setFont(new Font("Arial", Font.PLAIN, 20));
        noteTitleField.setEditable(true);

        // Content Area
        noteContentArea = new JEditorPane();
        noteContentArea.setEditable(true);
        noteContentArea.setFont(new Font("Arial", Font.PLAIN, 20));

        // Images List
        imagesListModel = new DefaultListModel<>();
        imagesList = new JList<>(imagesListModel);
        imagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imagesList.setVisibleRowCount(2);
        imagesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        imagesList.setFixedCellWidth(THUMBNAIL_WIDTH);
        imagesList.setFixedCellHeight(THUMBNAIL_HEIGHT);

        // Sketches List
        sketchesListModel = new DefaultListModel<>();
        sketchesList = new JList<>(sketchesListModel);
        sketchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sketchesList.setVisibleRowCount(2);
        sketchesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        sketchesList.setFixedCellWidth(THUMBNAIL_WIDTH);
        sketchesList.setFixedCellHeight(THUMBNAIL_HEIGHT);

        // Buttons
        editButton = new JButton("Edit Note");
        deleteButton = new JButton("Delete Note");
        saveButton = new JButton("Save Note");
        backButton = new JButton("Back to Main Menu");
        sketchButton = new JButton("New Sketch");
        addImageButton = new JButton("Add Image");
    }

    private void setupLayout() {
        // Left Panel (Notes List)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JScrollPane(notesList), BorderLayout.CENTER);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Right Panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.add(noteTitleField, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(noteContentArea), BorderLayout.CENTER);

        // Media Panel (Images and Sketches)
        JPanel mediaPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        // Images Section
        JPanel imagesPanel = new JPanel(new BorderLayout());
        imagesPanel.setBorder(BorderFactory.createTitledBorder("Images"));
        imagesPanel.add(new JScrollPane(imagesList), BorderLayout.CENTER);
        imagesPanel.add(addImageButton, BorderLayout.SOUTH);
        
        // Sketches Section
        JPanel sketchesPanel = new JPanel(new BorderLayout());
        sketchesPanel.setBorder(BorderFactory.createTitledBorder("Sketches"));
        sketchesPanel.add(new JScrollPane(sketchesList), BorderLayout.CENTER);
        sketchesPanel.add(sketchButton, BorderLayout.SOUTH);

        mediaPanel.add(imagesPanel);
        mediaPanel.add(sketchesPanel);
        rightPanel.add(mediaPanel, BorderLayout.SOUTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        // Main Layout
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        notesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displayNoteContent(notesList.getSelectedValue());
            }
        });

        imagesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ImageIcon selected = imagesList.getSelectedValue();
                    if (selected != null) {
                        showExpandedImage(selected);
                    }
                }
            }
        });

        sketchesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ImageIcon selected = sketchesList.getSelectedValue();
                    if (selected != null) {
                        showExpandedImage(selected);
                    }
                }
            }
        });

        editButton.addActionListener(e -> editNote());
        deleteButton.addActionListener(e -> deleteNote());
        saveButton.addActionListener(e -> saveNote());
        backButton.addActionListener(e -> {
            new MainMenuFrame(username).setVisible(true);
            dispose();
        });
        sketchButton.addActionListener(e -> openSketchPad());
        addImageButton.addActionListener(e -> addImageToNote());
    }

    private void showExpandedImage(ImageIcon thumbnail) {
        JDialog dialog = new JDialog(this, "Expanded View", true);
        JLabel label = new JLabel(new ImageIcon(thumbnail.getImage().getScaledInstance(-1, 400, Image.SCALE_SMOOTH)));
        dialog.add(label);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void openSketchPad() {
        SketchPad sketchPad = new SketchPad();
        sketchPad.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (sketchPad.getSavedImage() != null) {
                    ImageIcon thumbnail = createThumbnail(sketchPad.getSavedImage());
                    sketchesListModel.addElement(thumbnail);
                    // Save sketch to file system and update note's sketch paths
                    saveSketchtoDisk(sketchPad.getSavedImage());
                }
            }
        });
        sketchPad.setVisible(true);
    }

    private void saveSketchtoDisk(BufferedImage image) {
        // TODO: Implement saving sketch to disk
        // This should:
        // 1. Generate a unique filename
        // 2. Save the image to the appropriate directory
        // 3. Add the path to the current note's sketch paths
        // 4. Update the note in the file system
    }

    private void displayNoteContent(String selectedTitle) {
        for (Note note : notes) {
            if (note.getTitle().equals(selectedTitle)) {
                if (note instanceof SecureNote) {
                    SecureNote secureNote = (SecureNote) note;
                    String enteredPassword = JOptionPane.showInputDialog(this, "Enter password to view this note:");
                    if (enteredPassword != null && enteredPassword.equals(secureNote.getPassword())) {
                        loadNoteContent(secureNote);
                    } else {
                        JOptionPane.showMessageDialog(this, "Incorrect password.");
                        return;
                    }
                } else {
                    loadNoteContent(note);
                }
                break;
            }
        }
    }

    private void loadNoteContent(Note note) {
        noteContentArea.setText(note.getContent());
        noteTitleField.setText(note.getTitle());

        // Clear existing media
        imagesListModel.clear();
        sketchesListModel.clear();

        // Load images
        if (note.getImagePaths() != null) {
            for (String imagePath : note.getImagePaths()) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    ImageIcon original = new ImageIcon(imagePath);
                    ImageIcon thumbnail = createThumbnail(original.getImage());
                    imagesListModel.addElement(thumbnail);
                }
            }
        }

        // Load sketches
        /*if (note.getImagePaths() != null) {
            for (String sketchPath : note.getImagePaths()) {
                File sketchFile = new File(sketchPath);
                if (sketchFile.exists()) {
                    ImageIcon original = new ImageIcon(sketchPath);
                    ImageIcon thumbnail = createThumbnail(original.getImage());
                    sketchesListModel.addElement(thumbnail);
                }
            }
        }*/
    }

    private void editNote() {
        String selectedTitle = notesList.getSelectedValue();
        if (selectedTitle == null) {
            JOptionPane.showMessageDialog(this, "Please select a note to edit.");
            return;
        }
        noteTitleField.setEditable(true);
        noteContentArea.setEditable(true);
    }

    private void saveNote() {
        String newTitle = noteTitleField.getText();
        String newContent = noteContentArea.getText();

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both title and content must be filled.");
            return;
        }

        String password = null;
        int option = JOptionPane.showConfirmDialog(this, "Is this a secure note?", "Secure Note", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            password = JOptionPane.showInputDialog(this, "Enter a password for this secure note:");
        }

        Note existingNote = null;
        for (Note note : notes) {
            if (note.getTitle().equals(newTitle)) {
                existingNote = note;
                break;
            }
        }

        if (existingNote != null) {
            updateExistingNote(existingNote, newContent, password);
        } else {
            createNewNote(newTitle, newContent, password);
        }

        noteTitleField.setEditable(false);
        noteContentArea.setEditable(false);
    }

    private void updateExistingNote(Note existingNote, String newContent, String password) {
        existingNote.setContent(newContent);
        if (existingNote instanceof SecureNote && password != null) {
            ((SecureNote) existingNote).setPassword(password);
        }
        boolean success = FileManager.updateNoteInFile(existingNote, existingNote, username);
        if (success) {
            JOptionPane.showMessageDialog(this, "Note updated successfully.");
            displayNoteContent(existingNote.getTitle());
        } else {
            JOptionPane.showMessageDialog(this, "Error updating note.");
        }
    }

    private void createNewNote(String title, String content, String password) {
        Note newNote;
        if (password != null) {
            newNote = new SecureNote(title, content, new ArrayList<>(), username, password);
        } else {
            newNote = new Note(title, content, new ArrayList<>(), username);
        }
        notes.add(newNote);
        notesListModel.addElement(title);
        boolean success = FileManager.saveNewNoteToFile(newNote, username);
        if (success) {
            JOptionPane.showMessageDialog(this, "New note saved successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Error saving new note.");
        }
    }

    private void deleteNote() {
        String selectedTitle = notesList.getSelectedValue();
        if (selectedTitle == null) {
            JOptionPane.showMessageDialog(this, "Please select a note to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this note?", 
                                                  "Delete Note", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Note noteToDelete = null;
            for (Note note : notes) {
                if (note.getTitle().equals(selectedTitle)) {
                    noteToDelete = note;
                    break;
                }
            }

            if (noteToDelete != null && FileManager.deleteNoteFromFile(noteToDelete, username)) {
                notes.remove(noteToDelete);
                notesListModel.removeElement(selectedTitle);
                noteContentArea.setText("");
                imagesListModel.clear();
                sketchesListModel.clear();
                JOptionPane.showMessageDialog(this, "Note deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting note.");
            }
        }
    }

    private void addImageToNote() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            ImageIcon original = new ImageIcon(selectedFile.getAbsolutePath());
            ImageIcon thumbnail = createThumbnail(original.getImage());
            imagesListModel.addElement(thumbnail);
            // TODO: Save image path to note and update file
            JOptionPane.showMessageDialog(this, "Image added successfully.");
        }
    }

    private ImageIcon createThumbnail(Image image) {
        Image thumbnail = image.getScaledInstance(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(thumbnail);
    }
    /*private void resizeImage(String imagePath) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            
            // Calculate new dimensions while maintaining aspect ratio
            int targetWidth = imagePanel.getWidth();
            int targetHeight = imagePanel.getHeight();
            
            double widthRatio = (double) targetWidth / originalImage.getWidth();
            double heightRatio = (double) targetHeight / originalImage.getHeight();
            double ratio = Math.min(widthRatio, heightRatio);
            
            int scaledWidth = (int) (originalImage.getWidth() * ratio);
            int scaledHeight = (int) (originalImage.getHeight() * ratio);
            
            Image scaledImage = originalImage.getScaledInstance(
                scaledWidth,
                scaledHeight,
                Image.SCALE_SMOOTH
            );
            
            ImageIcon imageIcon = new ImageIcon(scaledImage);
            imageLabel.setIcon(imageIcon);
            
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error resizing image: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }*/
}