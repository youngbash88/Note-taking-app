package com.mycompany.notetakingapp;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static String getUserSubDir(String username, String subDirName) {
        String userDir = getUserDir(username);
        String subDirPath = Paths.get(userDir, subDirName).toString();
        File subDir = new File(subDirPath);
        if (!subDir.exists()) {
            subDir.mkdirs();
        }
        return subDirPath;
    }

    // Updated saveNoteToFile method to handle List<String> imagePaths
    public static void saveNoteToFile(Note note, String username) {
        String notesFilePath = getUserFilePath(username, "notes.txt");
        File notesFile = new File(notesFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(notesFile, true))) {
            writer.write("Title: " + note.getTitle());
            writer.newLine();
            writer.write("Content: " + note.getContent());
            writer.newLine();
            writer.write("Image Path: " + String.join(",", note.getImagePaths())); // Join paths with comma
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
            if (notes.get(i).equals(oldNote)) {
                notes.get(i).setTitle(updatedNote.getTitle());
                notes.get(i).setContent(updatedNote.getContent());
                notes.get(i).setImagePath(updatedNote.getImagePaths()); // Using the new method
                notes.get(i).setSketchPath(updatedNote.getSketchPath());
                noteFound = true;
                break;
            }
        }

        if (noteFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Note note : notes) {
                    writer.write("Title: " + note.getTitle());
                    writer.newLine();
                    writer.write("Content: " + note.getContent());
                    writer.newLine();
                    writer.write("Image Path: " + String.join(",", note.getImagePaths())); // Join paths with comma
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
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
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
                    title = line.substring(7);
                } else if (line.startsWith("Content: ")) {
                    content = line.substring(9);
                } else if (line.startsWith("Image Path: ")) {
                    imagePaths = line.substring(12);
                } else if (line.startsWith("Sketch Path: ")) {
                    sketchPath = line.substring(13);
                } else if (line.startsWith("Password: ")) {
                    password = line.substring(10);
                } else if (line.equals("---- End of Note ----")) {
                    if (title != null && content != null) {
                        // Convert comma-separated image paths to List
                        List<String> imagePathsList = new ArrayList<>();
                        if (imagePaths != null && !imagePaths.trim().isEmpty()) {
                            imagePathsList.addAll(Arrays.asList(imagePaths.split(",")));
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

    // Other methods remain unchanged
    // ... (saveSketchToFile, saveImageToFile, saveUserToFile, validateUser, loadUsersFromFile, deleteNoteFromFile)
    // Updated deleteNoteFromFile method to handle List<String> imagePaths
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
