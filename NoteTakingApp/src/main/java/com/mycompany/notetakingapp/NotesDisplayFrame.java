package com.mycompany.notetakingapp;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NotesDisplayFrame extends JFrame {
    private JList<String> notesList;
    private DefaultListModel<String> notesListModel;

    public NotesDisplayFrame(List<Note> notes) {
        setTitle("View Notes");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        notesListModel = new DefaultListModel<>();
        notesList = new JList<>(notesListModel);

        for (Note note : notes) {
            notesListModel.addElement(note.getTitle());
        }

        setLayout(new BorderLayout());
        add(new JScrollPane(notesList), BorderLayout.CENTER);
    }
}
