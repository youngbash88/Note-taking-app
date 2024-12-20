package com.mycompany.notetakingapp;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {
    
    
    public static final String BASE_FOLDER_PATH = "users";
    private static final String NOTE_FILE = "notes.txt";  // File to store notes
    private static final String USER_FILE = "users.txt";  // File to store user accounts

    // Save a note to the user's file
    public static void saveNoteToFile(Note note, String username) {
        try {
            File file = new File(username + "_" + NOTE_FILE);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write("Title: " + note.getTitle());
                writer.newLine();
                writer.write("Content: " + note.getContent());
                writer.newLine();
                writer.write("Image Path: " + note.getImagePath());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save user to file with hashed password
    public static boolean saveUserToFile(User user) {
        Map<String, String> users = loadUsersFromFile();  // Load existing users into a Map

        // Check if the username already exists
        if (users.containsKey(user.getUsername())) {
            System.out.println("Username already exists: " + user.getUsername());
            return false;  // Return false if the username already exists
        }
        // Create user directory
        String userDirPath = BASE_FOLDER_PATH + File.separator + user.getUsername();
        File userDir = new File(userDirPath);
        if (!userDir.exists()) {
        if (!userDir.mkdirs()) {
            System.out.println("Failed to create directory for user: " + user.getUsername());
            return false;
        }
    }
        // Hash the password before saving
        String hashedPassword = PasswordHash.hashPassword(user.getPassword());
    

        // Append the new user with hashed password to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(user.getUsername() + ":" + hashedPassword);
            writer.newLine();
            return true;  // Return true for success
        } catch (IOException e) {
            e.printStackTrace();
            return false;  // Return false if an error occurs
        }
    }

    // Load notes from the user's file
    public static List<Note> loadNotesFromFile(String username) {
        File file = new File(username + "_" + NOTE_FILE);
        List<Note> notes = new ArrayList<>();
        if (!file.exists()) {
            return notes; // Return empty if the file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String title = null, content = null, imagePath = null, sketchPath = null, password = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Title: ")) {
                    title = line.substring(7);
                } else if (line.startsWith("Content: ")) {
                    content = line.substring(9);
                } else if (line.startsWith("Image Path: ")) {
                    imagePath = line.substring(12);
                } else if (line.startsWith("Sketch Path: ")) {
                    sketchPath = line.substring(13);
                } else if (line.startsWith("Password: ")) {
                    password = line.substring(10);
                } else if (line.equals("---- End of Note ----")) {
                    if (title != null && content != null && imagePath != null && sketchPath != null) {
                        Note note = (password != null)
                                ? new SecureNote(title, content, imagePath, sketchPath, password)
                                : new Note(title, content, imagePath, sketchPath);
                        notes.add(note);
                    }
                    title = content = imagePath = sketchPath = password = null; // Reset fields
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notes;
    }

    // Delete a note from the user's file
    public static boolean deleteNoteFromFile(Note note, String username) {
        List<Note> notes = loadNotesFromFile(username);
        File file = new File(username + "_" + NOTE_FILE);

        // If no notes exist or the file doesn't exist, return false
        if (notes.isEmpty() || !file.exists()) {
            return false;
        }

        // Remove the note that matches the one to delete
        boolean isDeleted = notes.removeIf(currentNote -> currentNote.equals(note));

        // If no note was deleted, return false
        if (!isDeleted) {
            return false;
        }

        // Write the remaining notes back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Note currentNote : notes) {
                writer.write("Title: " + currentNote.getTitle());
                writer.newLine();
                writer.write("Content: " + currentNote.getContent());
                writer.newLine();
                writer.write("Image Path: " + currentNote.getImagePath());
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
            return true;  // Successfully deleted and saved
        } catch (IOException e) {
            e.printStackTrace();
            return false;  // Error occurred during file writing
        }
    }

    // Update a note in the user's file
    public static boolean updateNoteInFile(Note oldNote, Note updatedNote, String username) {
        File file = new File(username + "_" + NOTE_FILE);
        List<Note> notes = loadNotesFromFile(username);

        // Find and update the specific note
        boolean noteFound = false;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).equals(oldNote)) { // Assuming you have overridden equals() in Note
                // Update the fields of the note
                notes.get(i).setTitle(updatedNote.getTitle());
                notes.get(i).setContent(updatedNote.getContent());
                notes.get(i).setImagePath(updatedNote.getImagePath());
                notes.get(i).setSketchPath(updatedNote.getSketchPath());

                noteFound = true;
                break;
            }
        }

        // If the note was found, rewrite the file
        if (noteFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Note note : notes) {
                    writer.write("Title: " + note.getTitle());
                    writer.newLine();
                    writer.write("Content: " + note.getContent());
                    writer.newLine();
                    writer.write("Image Path: " + note.getImagePath());
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

        return false; // Return false if the note was not found or an error occurred
    }

    // Validate user credentials with hashed password
    public static boolean validateUser(String username, String password) {
    Map<String, String> users = loadUsersFromFile();
    String hashedPassword = PasswordHash.hashPassword(password);

    System.out.println("Validating user: " + username);
    System.out.println("Entered hashed password: " + hashedPassword);

    if (users.containsKey(username)) {
        String storedPassword = users.get(username);
        System.out.println("Stored hashed password for user '" + username + "': " + storedPassword);
        return storedPassword.equals(hashedPassword);
    } else {
        System.out.println("Username not found: " + username);
        return false;
    }
}


    // Load all users from the user file into a Map
    static Map<String, String> loadUsersFromFile() {
        Map<String, String> users = new HashMap<>();
        File file = new File(USER_FILE);

        if (!file.exists()) {
            System.out.println("User file not found: " + USER_FILE);
            return users;  // Return empty map
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                } else {
                    System.out.println("Invalid line in user file: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

}
