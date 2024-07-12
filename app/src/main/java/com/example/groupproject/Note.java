package com.example.groupproject;

import java.io.Serializable;

public class Note implements Serializable {
    private String noteId;
    private String title;
    private String description;

    // Add getters and setters
    // Constructor, if needed

    // Default constructor for Firebase
    public Note() {
    }

    public Note(String noteId, String title, String description) {
        this.noteId = noteId;
        this.title = title;
        this.description = description;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
