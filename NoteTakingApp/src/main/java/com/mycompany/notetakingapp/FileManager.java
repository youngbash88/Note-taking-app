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

    // Helper method to convert relative path to absolute path
    public static String toAbsolutePath(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return null;
        }

        File file = new File(relativePath);
        if (file.isAbsolute()) {
            return relativePath;
        }

        // First try from current directory
        File fromCurrent = new File(System.getProperty("user.dir"), relativePath);
        if (fromCurrent.exists()) {
            return fromCurrent.getAbsolutePath();
        }

        // Then try from base folder
        File fromBase = new File(BASE_FOLDER_PATH, relativePath);
        if (fromBase.exists()) {
            return fromBase.getAbsolutePath();
        }

        // Finally try direct path
        if (file.exists()) {
            return file.getAbsolutePath();
        }

        // Return the path from current directory as default
        return fromCurrent.getAbsolutePath();
    }

    // Helper method to ensure path is relative to BASE_FOLDER_PATH
    private static String toRelativePath(String absolutePath, String username) {
        if (absolutePath == null || absolutePath.trim().isEmpty()) {
            return "";
        }

        String basePath = new File(System.getProperty("user.dir")).getAbsolutePath();
        String relativePath = absolutePath;

        if (absolutePath.startsWith(basePath)) {
            relativePath = absolutePath.substring(basePath.length() + 1);
        }

        // Ensure path starts with users/username
        if (!relativePath.startsWith("users")) {
            if (relativePath.contains("images")) {
                relativePath = Paths.get("users", username, "images", new File(relativePath).getName()).toString();
            } else if (relativePath.contains("sketches")) {
                relativePath = Paths.get("users", username, "sketches", new File(relativePath).getName()).toString();
            }
        }

        return relativePath;
    }

    // Updated saveNoteToFile method to handle List<String> imagePaths
    public static void saveNoteToFile(Note note, String username) {
        String notesFilePath = getUserFilePath(username, "notes.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(notesFilePath, true))) {
            writer.write("Title: " + escapeNewlines(note.getTitle()));
            writer.newLine();
            writer.write("Content: " + escapeNewlines(note.getContent()));
            writer.newLine();
            
            // Save image paths
            StringBuilder imagePaths = new StringBuilder();
            for (String path : note.getImagePaths()) {
                if (path != null && !path.trim().isEmpty()) {
                    imagePaths.append(toRelativePath(path, username)).append(",");
                }
            }
            String imagePathsStr = imagePaths.length() > 0 ? imagePaths.substring(0, imagePaths.length() - 1) : "";
            writer.write("Image Path: " + imagePathsStr);
            writer.newLine();
            
            // Save sketch paths
            StringBuilder sketchPaths = new StringBuilder();
            for (String path : note.getSketchPaths()) {
                if (path != null && !path.trim().isEmpty()) {
                    sketchPaths.append(toRelativePath(path, username)).append(",");
                }
            }
            String sketchPathsStr = sketchPaths.length() > 0 ? sketchPaths.substring(0, sketchPaths.length() - 1) : "";
            writer.write("Sketch Path: " + sketchPathsStr);
            writer.newLine();

            if (note instanceof SecureNote) {
                writer.write("Password: " + ((SecureNote) note).getPassword());
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
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getTitle().equals(oldNote.getTitle())) {
                notes.set(i, updatedNote);
                noteFound = true;
                break;
            }
        }

        if (!noteFound) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Note note : notes) {
                writer.write("Title: " + escapeNewlines(note.getTitle()));
                writer.newLine();
                writer.write("Content: " + escapeNewlines(note.getContent()));
                writer.newLine();
                
                // Save image paths
                StringBuilder imagePaths = new StringBuilder();
                for (String path : note.getImagePaths()) {
                    if (path != null && !path.trim().isEmpty()) {
                        imagePaths.append(toRelativePath(path, username)).append(",");
                    }
                }
                String imagePathsStr = imagePaths.length() > 0 ? imagePaths.substring(0, imagePaths.length() - 1) : "";
                writer.write("Image Path: " + imagePathsStr);
                writer.newLine();
                
                // Save sketch paths
                StringBuilder sketchPaths = new StringBuilder();
                for (String path : note.getSketchPaths()) {
                    if (path != null && !path.trim().isEmpty()) {
                        sketchPaths.append(toRelativePath(path, username)).append(",");
                    }
                }
                String sketchPathsStr = sketchPaths.length() > 0 ? sketchPaths.substring(0, sketchPaths.length() - 1) : "";
                writer.write("Sketch Path: " + sketchPathsStr);
                writer.newLine();

                if (note instanceof SecureNote) {
                    writer.write("Password: " + ((SecureNote) note).getPassword());
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
            String title = null, content = null, imagePaths = null, sketchPaths = null, password = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Title: ")) {
                    title = unescapeNewlines(line.substring(7));
                } else if (line.startsWith("Content: ")) {
                    content = unescapeNewlines(line.substring(9));
                } else if (line.startsWith("Image Path: ")) {
                    imagePaths = line.substring(12);
                } else if (line.startsWith("Sketch Path: ")) {
                    sketchPaths = line.substring(13).trim();
                } else if (line.startsWith("Password: ")) {
                    password = line.substring(10);
                } else if (line.equals("---- End of Note ----")) {
                    if (title != null && content != null) {
                        List<String> imagePathsList = new ArrayList<>();
                        if (imagePaths != null && !imagePaths.trim().isEmpty()) {
                            String[] paths = imagePaths.split(",");
                            for (String path : paths) {
                                String trimmedPath = path.trim();
                                if (!trimmedPath.isEmpty()) {
                                    imagePathsList.add(toAbsolutePath(trimmedPath));
                                }
                            }
                        }

                        List<String> sketchPathsList = new ArrayList<>();
                        if (sketchPaths != null && !sketchPaths.trim().isEmpty()) {
                            String[] paths = sketchPaths.split(",");
                            for (String path : paths) {
                                String trimmedPath = path.trim();
                                if (!trimmedPath.isEmpty()) {
                                    sketchPathsList.add(toAbsolutePath(trimmedPath));
                                }
                            }
                        }

                        Note note;
                        if (password != null) {
                            note = new SecureNote(title, content, imagePathsList, sketchPathsList, password);
                        } else {
                            note = new Note(title, content, imagePathsList, sketchPathsList);
                        }
                        notes.add(note);
                    }
                    title = content = imagePaths = sketchPaths = password = null;
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

    // Helper method to escape newlines
    private static String escapeNewlines(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    // Helper method to unescape newlines
    private static String unescapeNewlines(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\n", "\n")
                .replace("\\r", "\r");
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
