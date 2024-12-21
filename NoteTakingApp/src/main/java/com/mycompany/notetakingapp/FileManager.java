package com.mycompany.notetakingapp;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class FileManager {

    public static final String BASE_FOLDER_PATH = "users";
    private static final String USER_FILE = "users.txt";

    // Existing directory management methods remain unchanged
    private static String getUserDir(String username) {
        String userDirPath = Paths.get(BASE_FOLDER_PATH, username).toString();
        File userDir = new File(userDirPath);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
        return userDirPath;
    }

    private static String getUserFilePath(String username, String fileName) {
        String userDir = getUserDir(username);
        return Paths.get(userDir, fileName).toString();
    }

    // Get or create the images directory for a user
    private static String getUserImagesDir(String username) {
        String imagesDirPath = Paths.get(getUserDir(username), "images").toString();
        File imagesDir = new File(imagesDirPath);
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        return imagesDirPath;
    }

    public static String getUserSketchDir(String username) {
        String sketchDirPath = Paths.get(getUserDir(username), "sketches").toString();
        File sketchDir = new File(sketchDirPath);
        if (!sketchDir.exists()) {
            sketchDir.mkdirs();
        }
        return sketchDirPath;
    }

    private static String getUserSubDir(String username, String subDirName) {
        String userDir = getUserDir(username);
        String subDirPath = Paths.get(userDir, subDirName).toString();
        File subDir = new File(subDirPath);
        if (!subDir.exists()) {
            subDir.mkdirs();
        }
        return subDirPath;
    }

    // Save an image file to the images directory for a user
    public static boolean saveImageToFile(String username, String imageName, InputStream imageData) {
        String imagesDir = getUserImagesDir(username);
        File imageFile = new File(Paths.get(imagesDir, imageName).toString());
        try (OutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = imageData.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String saveSketchToFile(String username, BufferedImage sketch) {
        try {
            String sketchDir = getUserSketchDir(username);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "sketch_" + timestamp + ".png";
            String filePath = Paths.get(sketchDir, fileName).toString();

            File outputFile = new File(filePath);
            ImageIO.write(sketch, "PNG", outputFile);

            // Return the relative path from the user directory
            return Paths.get("users", username, "sketches", fileName).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Updated saveNoteToFile method to handle List<String> imagePaths
    // Save a note to the notes file
    public static void saveNoteToFile(Note note, String username) {
        String notesFilePath = getUserFilePath(username, "notes.txt");
        File notesFile = new File(notesFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(notesFile, true))) {
            writer.write("Title: " + note.getTitle().replace("\n", "\\n"));
            writer.newLine();
            writer.write("Content: " + note.getContent().replace("\n", "\\n"));
            writer.newLine();

            // Save image paths as relative paths within the images folder
            List<String> relativeImagePaths = new ArrayList<>();
            for (String imagePath : note.getImagePaths()) {
                String relativePath = "users/" + username + "/images/" + new File(imagePath).getName();
                relativeImagePaths.add(relativePath);
            }
            writer.write("Image Path: " + String.join(",", relativeImagePaths));
            writer.newLine();

            writer.write("Sketch Path: " + (note.getSketchPath() != null ? note.getSketchPath() : ""));
            writer.newLine();

            if (note instanceof SecureNote) {
                SecureNote secureNote = (SecureNote) note;
                writer.write("Password: " + secureNote.getPassword());
                writer.newLine();
            }

            writer.write("---- End of Note ----");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveNewNoteToFile(Note newNote, String username) {
        try {
            // Assuming saveNoteToFile method handles saving logic for new notes
            saveNoteToFile(newNote, username);  // Reuse the method you've already implemented
            return true;  // Indicating success
        } catch (Exception e) { // Use general Exception if you want to catch all exceptions
            e.printStackTrace();
            return false;  // Indicating failure
        }
    }

    // Updated updateNoteInFile method to handle List<String> imagePaths
    public static boolean updateNoteInFile(Note oldNote, Note updatedNote, String username) {
        File file = new File(getUserFilePath(username, "notes.txt"));
        List<Note> notes = loadNotesFromFile(username);

        boolean noteFound = false;

        // Find and update the matching note
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).equals(oldNote)) {
                System.out.println("Debug: Found the note to update: " + oldNote.getTitle());
                notes.get(i).setTitle(updatedNote.getTitle());
                notes.get(i).setContent(updatedNote.getContent());
                notes.get(i).setImagePath(updatedNote.getImagePaths());
                notes.get(i).setSketchPath(updatedNote.getSketchPath());

                // If it's a SecureNote, handle the password field
                if (notes.get(i) instanceof SecureNote && updatedNote instanceof SecureNote) {
                    ((SecureNote) notes.get(i)).setPassword(((SecureNote) updatedNote).getPassword());
                }
                noteFound = true;
                break;
            }
        }

        if (!noteFound) {
            System.err.println("Debug: Note not found in the file.");
            return false;
        }

        File tempFile = new File(file.getParent(), "notes_temp.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            // Write all notes to the temporary file
            for (Note note : notes) {
                writer.write("Title: " + note.getTitle());
                writer.newLine();
                writer.write("Content: " + note.getContent());
                writer.newLine();
                writer.write("Image Path: " + String.join(",", note.getImagePaths()));
                writer.newLine();
                writer.write("Sketch Path: " + note.getSketchPath());
                writer.newLine();

                if (note instanceof SecureNote) {
                    SecureNote secureNote = (SecureNote) note;
                    writer.write("Password: " + secureNote.getPassword());
                    writer.newLine();
                }

                writer.write("---- End of Note ----");
                writer.newLine();
            }

            writer.flush();
            System.out.println("Debug: Successfully wrote to the temporary file.");
        } catch (IOException e) {
            System.err.println("Debug: Error writing to the temporary file - " + e.getMessage());
            e.printStackTrace();
            if (tempFile.exists()) {
                tempFile.delete(); // Cleanup temporary file
            }
            return false;
        }

        // Replace original file with the temporary file
        if (!tempFile.renameTo(file)) {
            System.err.println("Debug: Failed to rename the temporary file to the original file.");
            try {
                // Fallback: Copy content to the original file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)); BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                }

                System.out.println("Debug: Successfully replaced the original file using fallback.");
                tempFile.delete(); // Cleanup temporary file
                return true;
            } catch (IOException e) {
                System.err.println("Debug: Fallback failed - " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        System.out.println("Debug: Successfully replaced the original file.");
        return true;
    }

    // Updated loadNotesFromFile method to handle List<String> imagePaths
    public static List<Note> loadNotesFromFile(String username) {
        String notesFilePath = getUserFilePath(username, "notes.txt");
        File file = new File(notesFilePath);
        List<Note> notes = new ArrayList<>();
        if (!file.exists()) {
            return notes;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String title = null, content = null, imagePaths = null, sketchPath = null, password = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Title: ")) {
                    title = line.substring(7).replace("\\n", "\n");
                } else if (line.startsWith("Content: ")) {
                    content = line.substring(9).replace("\\n", "\n");
                } else if (line.startsWith("Image Path: ")) {
                    imagePaths = line.substring(12);
                } else if (line.startsWith("Sketch Path: ")) {
                    sketchPath = line.substring(13);
                    // Convert relative path to absolute path if needed
                    if (!sketchPath.isEmpty()) {
                        File sketchFile = new File(sketchPath);
                        if (!sketchFile.exists()) {
                            sketchPath = Paths.get(System.getProperty("user.dir"), sketchPath).toString();
                        }
                    }
                } else if (line.startsWith("Password: ")) {
                    password = line.substring(10);
                } else if (line.equals("---- End of Note ----")) {
                    if (title != null && content != null) {
                        // Convert image paths
                        List<String> imagePathsList = new ArrayList<>();
                        if (imagePaths != null && !imagePaths.trim().isEmpty()) {
                            for (String path : imagePaths.split(",")) {
                                File imgFile = new File(path);
                                if (!imgFile.exists()) {
                                    path = Paths.get(System.getProperty("user.dir"), path).toString();
                                }
                                imagePathsList.add(path);
                            }
                        }

                        Note note;
                        if (password != null) {
                            note = new SecureNote(title, content, imagePathsList, sketchPath, password);
                        } else {
                            note = new Note(title, content, imagePathsList, sketchPath);
                        }
                        notes.add(note);
                    }
                    title = content = imagePaths = sketchPath = password = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notes;
    }

    static Map<String, String> loadUsersFromFile() {
        Map<String, String> users = new HashMap<>();
        File file = new File(USER_FILE);

        if (!file.exists()) {
            return users; // Return empty map if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static boolean saveUserToFile(User user) {
        Map<String, String> users = loadUsersFromFile();

        if (users.containsKey(user.getUsername())) {
            System.out.println("Username already exists: " + user.getUsername());
            return false;
        }

        getUserDir(user.getUsername());
        String hashedPassword = PasswordHash.hashPassword(user.getPassword());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(user.getUsername() + ":" + hashedPassword);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteNoteFromFile(Note note, String username) {
        List<Note> notes = loadNotesFromFile(username);
        File file = new File(getUserFilePath(username, "notes.txt"));

        if (notes.isEmpty() || !file.exists()) {
            return false;
        }

        boolean isDeleted = notes.removeIf(currentNote -> currentNote.equals(note));

        if (!isDeleted) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Note currentNote : notes) {
                writer.write("Title: " + currentNote.getTitle());
                writer.newLine();
                writer.write("Content: " + currentNote.getContent());
                writer.newLine();
                writer.write("Image Path: " + String.join(",", currentNote.getImagePaths())); // Join paths with comma
                writer.newLine();
                writer.write("Sketch Path: " + currentNote.getSketchPath());
                writer.newLine();

                if (currentNote instanceof SecureNote) {
                    SecureNote secureNote = (SecureNote) currentNote;
                    writer.write("Password: " + secureNote.getPassword());
                    writer.newLine();
                }

                writer.write("---- End of Note ----");
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
