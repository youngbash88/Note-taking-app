package com.mycompany.notetakingapp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private final String baseFolderPath;

    public FileManager(String baseFolderPath) {
        this.baseFolderPath = baseFolderPath;
        createBaseFolder();
    }

    // Ensure the base folder exists
    private void createBaseFolder() {
        File baseFolder = new File(baseFolderPath);
        if (!baseFolder.exists()) {
            baseFolder.mkdirs();
        }
    }

    // Save a note to a user's folder
    public void saveNote(Note note, User user) throws IOException {
        String userFolderPath = baseFolderPath + "/" + user.getUsername();
        File userFolder = new File(userFolderPath);

        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }

        // Save the note as a serialized object or JSON
        String noteFilePath = userFolderPath + "/" + note.getTitle() + ".note";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(noteFilePath))) {
            oos.writeObject(note);
        }
    }

    // Load all notes for a user
    public List<Note> loadNotes(User user) throws IOException, ClassNotFoundException {
        String userFolderPath = baseFolderPath + "/" + user.getUsername();
        File userFolder = new File(userFolderPath);

        List<Note> notes = new ArrayList<>();
        if (userFolder.exists() && userFolder.isDirectory()) {
            for (File file : userFolder.listFiles()) {
                if (file.getName().endsWith(".note")) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        Note note = (Note) ois.readObject();
                        notes.add(note);
                    }
                }
            }
        }
        return notes;
    }

    // Save an image file
    public void saveImage(Image image, User user) throws IOException {
        String userFolderPath = baseFolderPath + "/" + user.getUsername() + "/images";
        File imageFolder = new File(userFolderPath);

        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }

        File sourceFile = new File(image.getFilePath());
        File destinationFile = new File(userFolderPath + "/" + sourceFile.getName());

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    // Save a sketch file
    public void saveSketch(Sketch sketch, User user) throws IOException {
        String userFolderPath = baseFolderPath + "/" + user.getUsername() + "/sketches";
        File sketchFolder = new File(userFolderPath);

        if (!sketchFolder.exists()) {
            sketchFolder.mkdirs();
        }

        File sourceFile = new File(sketch.getFilePath());
        File destinationFile = new File(userFolderPath + "/" + sourceFile.getName());

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
