package com.mycompany.notetakingapp;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

    private static final String NOTE_FILE = "notes.txt";  // File to store notes
    private static final String USER_FILE = "users.txt";  // File to store user accounts

    // Save a note to the user's file
    public static void saveNoteToFile(Note note, String username) {
        try {
            File file = new File(username + "_" + NOTE_FILE);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

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
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save user to file with hashed password
    public static boolean saveUserToFile(User user) {
        Map<String, String> users = loadUsersFromFile();  // Load existing users into a Map

        // Check if the username already exists
        if (users.containsKey(user.getUsername())) {
            return false;  // Return false if the username already exists
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
    public static void deleteNoteFromFile(Note note, String username) {
        List<Note> notes = loadNotesFromFile(username);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(username + "_" + NOTE_FILE))) {
            for (Note currentNote : notes) {
                if (!currentNote.equals(note)) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update a note in the user's file
    public static void updateNoteInFile(Note oldNote, Note newNote, String username) {
        List<Note> notes = loadNotesFromFile(username);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(username + "_" + NOTE_FILE))) {
            for (Note currentNote : notes) {
                if (currentNote.equals(oldNote)) {
                    writer.write("Title: " + newNote.getTitle());
                    writer.newLine();
                    writer.write("Content: " + newNote.getContent());
                    writer.newLine();
                    writer.write("Image Path: " + newNote.getImagePath());
                    writer.newLine();
                    writer.write("Sketch Path: " + newNote.getSketchPath());
                    writer.newLine();
                    if (newNote instanceof SecureNote) {
                        SecureNote secureNote = (SecureNote) newNote;
                        writer.write("Password: " + secureNote.getPassword());
                        writer.newLine();
                    }
                    writer.write("---- End of Note ----");
                    writer.newLine();
                } else {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Validate user credentials with hashed password
    public static boolean validateUser(String username, String password) {
        Map<String, String> users = loadUsersFromFile();
        String hashedPassword = PasswordHash.hashPassword(password);
        return users.containsKey(username) && users.get(username).equals(hashedPassword);
    }

    // Load all users from the user file into a Map
    static Map<String, String> loadUsersFromFile() {
        Map<String, String> users = new HashMap<>();
        File file = new File(USER_FILE);

        if (!file.exists()) {
            return users;
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
}
