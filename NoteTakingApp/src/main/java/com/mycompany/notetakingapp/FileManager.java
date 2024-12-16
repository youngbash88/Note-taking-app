package com.mycompany.notetakingapp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final String NOTE_FILE = "notes.txt";  // Store notes in a readable text file (not .ser)

    // Save a note to the user's file in a human-readable format
    public static void saveNoteToFile(Note note, String username) {
        try {
            // Define the file path for each user, with their specific username
            File file = new File(username + "_" + NOTE_FILE);
            // Create a BufferedWriter to write to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true)); // 'true' for appending

            // Write the note details in a readable format
            writer.write("Title: " + note.getTitle());
            writer.newLine();
            writer.write("Content: " + note.getContent());
            writer.newLine();
            writer.write("Image Path: " + note.getImagePath());
            writer.newLine();
            writer.write("Sketch Path: " + note.getSketchPath());
            writer.newLine();

            // If it's a SecureNote, include the password
            if (note instanceof SecureNote) {
                SecureNote secureNote = (SecureNote) note;
                writer.write("Password: " + secureNote.getPassword());
                writer.newLine();
            }

            // Add a separator to clearly mark the end of the note
            writer.write("---- End of Note ----");
            writer.newLine();

            // Close the BufferedWriter to save the data
            writer.close();

        } catch (IOException e) {
            e.printStackTrace(); // Handle IOExceptions during writing to file
        }
    }

    // Load notes from a text file as plain text lines (return a List of Strings)
   // Load notes from a text file and return a list of Note objects
public static List<Note> loadNotesFromFile(String username) {
    File file = new File(username + "_" + NOTE_FILE);  // File path based on username
    List<Note> notes = new ArrayList<>();
    
    if (!file.exists()) {
        return notes;  // Return an empty list if the file doesn't exist
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        String title = null, content = null, imagePath = null, sketchPath = null, password = null;

        // Read each line from the file
        while ((line = reader.readLine()) != null) {
            // Parse the note's properties based on specific tags
            if (line.startsWith("Title: ")) {
                title = line.substring(7);  // Extract title
            } else if (line.startsWith("Content: ")) {
                content = line.substring(9);  // Extract content
            } else if (line.startsWith("Image Path: ")) {
                imagePath = line.substring(12);  // Extract image path
            } else if (line.startsWith("Sketch Path: ")) {
                sketchPath = line.substring(13);  // Extract sketch path
            } else if (line.startsWith("Password: ")) {
                password = line.substring(10);  // Extract password if present
            } else if (line.equals("---- End of Note ----")) {
                // Once a complete note is read, create a Note or SecureNote object
                if (title != null && content != null && imagePath != null && sketchPath != null) {
                    Note note = (password != null)
                            ? new SecureNote(title, content, imagePath, sketchPath, password)
                            : new Note(title, content, imagePath, sketchPath);
                    notes.add(note);  // Add the note to the list
                }
                // Reset fields for the next note
                title = content = imagePath = sketchPath = password = null;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();  // Handle IOExceptions
    }
    return notes;  // Return the list of notes
}


    // Delete a note from the user's file (in the text format)
   // Delete a note from the user's file (in the text format)
public static void deleteNoteFromFile(Note note, String username) {
    List<Note> notes = loadNotesFromFile(username);  // Now works with Note objects
    try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(username + "_" + NOTE_FILE));
        for (Note currentNote : notes) {
            // Skip writing the note to file if it's the one to be deleted
            if (!currentNote.equals(note)) {
                // Write the note details back to the file
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
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();  // Handle IOExceptions during deletion
    }
}


    // Update a note in the user's file (in the text format)
   // Update a note in the user's file (in the text format)
public static void updateNoteInFile(Note oldNote, Note newNote, String username) {
    List<Note> notes = loadNotesFromFile(username);  // Now works with Note objects
    try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(username + "_" + NOTE_FILE));
        for (Note currentNote : notes) {
            if (currentNote.equals(oldNote)) {
                // Update the note with the new details
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
                // Write the existing note back without changes
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
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();  // Handle IOExceptions during updating
    }
}

}
