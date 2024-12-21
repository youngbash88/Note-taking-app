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
    private static final int THUMBNAIL_WIDTH = 50;
    private static final int THUMBNAIL_HEIGHT = 50;

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
        setSize(1100, 700);
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
        imagesList.setVisibleRowCount(1);
        imagesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        imagesList.setFixedCellWidth(THUMBNAIL_WIDTH);
        imagesList.setFixedCellHeight(THUMBNAIL_HEIGHT);

        // Sketches List
        sketchesListModel = new DefaultListModel<>();
        sketchesList = new JList<>(sketchesListModel);
        sketchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sketchesList.setVisibleRowCount(1);
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
        // Find the original image path
        String selectedTitle = notesList.getSelectedValue();
        Note selectedNote = null;
        for (Note note : notes) {
            if (note.getTitle().equals(selectedTitle)) {
                selectedNote = note;
                break;
            }
        }

        if (selectedNote == null) {
            return;
        }

        // Get the image path that corresponds to this thumbnail
        int selectedIndex = imagesList.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= selectedNote.getImagePaths().size()) {
            return;
        }

        String imagePath = selectedNote.getImagePaths().get(selectedIndex);

        try {
            // Load the full quality image
            File imgFile = new File(imagePath);
            if (!imgFile.exists()) {
                // Try with full path
                imgFile = new File(System.getProperty("user.dir"), imagePath);
            }

            if (imgFile.exists()) {
                BufferedImage fullImage = ImageIO.read(imgFile);
                if (fullImage != null) {
                    // Create a dialog to show the full image
                    JDialog dialog = new JDialog(this, "Image Viewer", true);

                    // Scale the image to fit the screen while maintaining aspect ratio
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    int maxWidth = (int) (screenSize.width * 0.8);
                    int maxHeight = (int) (screenSize.height * 0.8);

                    double scale = Math.min(
                            (double) maxWidth / fullImage.getWidth(),
                            (double) maxHeight / fullImage.getHeight()
                    );

                    int scaledWidth = (int) (fullImage.getWidth() * scale);
                    int scaledHeight = (int) (fullImage.getHeight() * scale);

                    // Create a high-quality scaled image
                    Image scaledImage = fullImage.getScaledInstance(
                            scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

                    JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                    JScrollPane scrollPane = new JScrollPane(imageLabel);

                    dialog.add(scrollPane);
                    dialog.setSize(scaledWidth + 50, scaledHeight + 50);
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading full-quality image.");
        }
    }

    private void openSketchPad() {
        SketchPad sketchPad = new SketchPad(username);
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

        // Set fields as non-editable initially
        noteTitleField.setEditable(false);
        noteContentArea.setEditable(false);

        // Clear existing media
        imagesListModel.clear();
        sketchesListModel.clear();

        // Load images with absolute paths
        if (note.getImagePaths() != null && !note.getImagePaths().isEmpty()) {
            for (String imagePath : note.getImagePaths()) {
                try {
                    File imgFile = new File(imagePath);
                    if (!imgFile.exists()) {
                        // Try with full path
                        imgFile = new File(System.getProperty("user.dir"), imagePath);
                    }

                    if (imgFile.exists()) {
                        BufferedImage img = ImageIO.read(imgFile);
                        if (img != null) {
                            // Create high-quality thumbnail
                            BufferedImage thumbnail = createHighQualityThumbnail(img);
                            imagesListModel.addElement(new ImageIcon(thumbnail));
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error loading image: " + imagePath);
                    e.printStackTrace();
                }
            }
        }

        // Load sketches
        if (note.getSketchPath() != null && !note.getSketchPath().isEmpty()) {
            try {
                File sketchFile = new File(note.getSketchPath());
                if (!sketchFile.exists()) {
                    sketchFile = new File(System.getProperty("user.dir"), note.getSketchPath());
                }

                if (sketchFile.exists()) {
                    BufferedImage sketch = ImageIO.read(sketchFile);
                    if (sketch != null) {
                        BufferedImage thumbnail = createHighQualityThumbnail(sketch);
                        sketchesListModel.addElement(new ImageIcon(thumbnail));
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading sketch: " + note.getSketchPath());
                e.printStackTrace();
            }
        }
    }

    private void addImageToNote() {
        String selectedTitle = notesList.getSelectedValue();
        if (selectedTitle == null) {
            JOptionPane.showMessageDialog(this, "Please select a note first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                BufferedImage originalImage = ImageIO.read(selectedFile);
                if (originalImage == null) {
                    JOptionPane.showMessageDialog(this, "Invalid image file.");
                    return;
                }

                // Generate unique filename
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();

                // Save to images directory
                boolean saved = FileManager.saveImageToFile(username, fileName,
                        new java.io.FileInputStream(selectedFile));

                if (saved) {
                    // Create thumbnail
                    ImageIcon thumbnail = createThumbnail(originalImage);
                    imagesListModel.addElement(thumbnail);

                    // Update note
                    for (Note note : notes) {
                        if (note.getTitle().equals(selectedTitle)) {
                            String imagePath = "users/" + username + "/images/" + fileName;
                            note.getImagePaths().add(imagePath);
                            FileManager.updateNoteInFile(note, note, username);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Image added successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error saving image.");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
            }
        }
    }

    private void editNote() {
        String selectedTitle = notesList.getSelectedValue();
        if (selectedTitle == null) {
            JOptionPane.showMessageDialog(this, "Please select a note to edit.");
            return;
        }

        // Enable editing
        noteTitleField.setEditable(true);
        noteContentArea.setEditable(true);

        /* Optional: Change the edit button text to indicate editing mode
    editButton.setText("Editing...");*/
    }

    private void saveNote() {
        String selectedTitle = notesList.getSelectedValue();
        String newTitle = noteTitleField.getText();
        String newContent = noteContentArea.getText();

        // If no title or content, show error message
        if (newTitle.isEmpty() || newContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both title and content must be filled.");
            return;
        }

        // If no note is selected (creating new note)
        if (selectedTitle == null) {
            createNewNote(newTitle, newContent);
            return;
        }

        // Find the existing note
        Note selectedNote = null;
        for (Note note : notes) {
            if (note.getTitle().equals(selectedTitle)) {
                selectedNote = note;
                break;
            }
        }

        if (selectedNote == null) {
            JOptionPane.showMessageDialog(this, "Error: Note not found.");
            return;
        }

        // Handle SecureNote separately
        Note oldNote;
        if (selectedNote instanceof SecureNote) {
            SecureNote secureNote = (SecureNote) selectedNote;
            oldNote = new SecureNote(
                    secureNote.getTitle(),
                    secureNote.getContent(),
                    secureNote.getImagePaths(),
                    secureNote.getSketchPath(),
                    secureNote.getPassword()
            );
        } else {
            oldNote = new Note(
                    selectedNote.getTitle(),
                    selectedNote.getContent(),
                    selectedNote.getImagePaths(),
                    selectedNote.getSketchPath()
            );
        }

        System.out.println("Debug: Selected note -> Title: " + selectedTitle + ", Content: " + selectedNote.getContent());
        System.out.println("Debug: Old note -> Title: " + oldNote.getTitle() + ", Content: " + oldNote.getContent());

        // If title has changed, ask if they want to update or create new
        if (!selectedTitle.equals(newTitle)) {
            int option = JOptionPane.showConfirmDialog(this,
                    "The title has been changed. Do you want to update the existing note?",
                    "Change Title",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.NO_OPTION) {
                createNewNote(newTitle, newContent);
                return;
            }

            // Create updatedNote with appropriate type
            Note updatedNote;
            if (selectedNote instanceof SecureNote) {
                SecureNote secureNote = (SecureNote) selectedNote;
                updatedNote = new SecureNote(
                        newTitle,
                        newContent,
                        secureNote.getImagePaths(),
                        secureNote.getSketchPath(),
                        secureNote.getPassword()
                );
            } else {
                updatedNote = new Note(newTitle, newContent, selectedNote.getImagePaths(), selectedNote.getSketchPath());
            }

            boolean success = FileManager.updateNoteInFile(oldNote, updatedNote, username);

            if (success) {
                JOptionPane.showMessageDialog(this, "Note updated successfully.");
                notesListModel.setElementAt(newTitle, notesList.getSelectedIndex());
                // Update the note in memory only after successful file update
                selectedNote.setTitle(newTitle);
                selectedNote.setContent(newContent);
            } else {
                JOptionPane.showMessageDialog(this, "Error updating note.");
            }
        } else {
            // Just update content of existing note
            Note updatedNote;
            if (selectedNote instanceof SecureNote) {
                SecureNote secureNote = (SecureNote) selectedNote;
                updatedNote = new SecureNote(
                        selectedTitle,
                        newContent,
                        secureNote.getImagePaths(),
                        secureNote.getSketchPath(),
                        secureNote.getPassword()
                );
            } else {
                updatedNote = new Note(selectedTitle, newContent, selectedNote.getImagePaths(), selectedNote.getSketchPath());
            }

            boolean success = FileManager.updateNoteInFile(oldNote, updatedNote, username);

            if (success) {
                JOptionPane.showMessageDialog(this, "Note updated successfully.");
                // Update the note in memory only after successful file update
                selectedNote.setContent(newContent);
            } else {
                JOptionPane.showMessageDialog(this, "Error updating note.");
            }
        }

        // Disable editing after saving
        noteTitleField.setEditable(false);
        noteContentArea.setEditable(false);
        editButton.setText("Edit Note");

        System.out.println("Debug: Save note process completed.");
    }

    private void createNewNote(String title, String content) {
        // Ask if the user wants to create a secure note
        int option = JOptionPane.showConfirmDialog(this,
                "Do you want to create this note as a secure note?",
                "Create Secure Note",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Secure note flow
            String password = JOptionPane.showInputDialog(this, "Enter a password for this secure note:");
            if (password != null && !password.isEmpty()) {
                Note newNote = new SecureNote(title, content, new ArrayList<>(), username, password);
                notes.add(newNote);
                notesListModel.addElement(title);
                boolean success = FileManager.saveNewNoteToFile(newNote, username);
                if (success) {
                    JOptionPane.showMessageDialog(this, "New secure note saved successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error saving new secure note.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                return;
            }
        } else {
            // Regular note flow
            Note newNote = new Note(title, content, new ArrayList<>(), username);
            notes.add(newNote);
            notesListModel.addElement(title);
            boolean success = FileManager.saveNewNoteToFile(newNote, username);
            if (success) {
                JOptionPane.showMessageDialog(this, "New note saved successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Error saving new note.");
            }
        }

        // Disable editing after saving
        noteTitleField.setEditable(false);
        noteContentArea.setEditable(false);
        editButton.setText("Edit Note");
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

    private ImageIcon createThumbnail(Image image) {
        // Create a BufferedImage with the desired thumbnail dimensions
        BufferedImage thumbnail = new BufferedImage(
                THUMBNAIL_WIDTH,
                THUMBNAIL_HEIGHT,
                BufferedImage.TYPE_INT_ARGB
        );

        // Draw the scaled instance of the original image
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null);
        g2d.dispose();

        // Convert the BufferedImage to an ImageIcon
        return new ImageIcon(thumbnail);
    }

    private BufferedImage createHighQualityThumbnail(BufferedImage original) {
        BufferedImage thumbnail = new BufferedImage(
                THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = thumbnail.createGraphics();
        // Enable high-quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(original, 0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null);
        g2d.dispose();

        return thumbnail;
    }

}
