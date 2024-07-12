package com.example.groupproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditNoteActivity extends AppCompatActivity {
    private EditText editNoteTitle, editNoteDescription;
    private Button buttonSaveChanges;
    private Note note;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        editNoteTitle = findViewById(R.id.editNoteTitle);
        editNoteDescription = findViewById(R.id.editNoteDescription);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        // Retrieve the note object from the intent
        note = (Note) getIntent().getSerializableExtra("note");

        // Populate the fields with the note's current data
        if (note != null) {
            editNoteTitle.setText(note.getTitle());
            editNoteDescription.setText(note.getDescription());
        }

        // Save changes to the note
        buttonSaveChanges.setOnClickListener(v -> {
            if (note != null) {
                note.setTitle(editNoteTitle.getText().toString());
                note.setDescription(editNoteDescription.getText().toString());
                saveNoteToDatabase();
            }
        });
    }

    private void saveNoteToDatabase() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString("username", null);

        if (username != null && note != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users")
                    .child(username).child("notes").child(note.getNoteId());
            databaseReference.setValue(note).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditNoteActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditNoteActivity.this, "Failed to update note", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(EditNoteActivity.this, "User not found or note is null", Toast.LENGTH_SHORT).show();
        }
    }
}
