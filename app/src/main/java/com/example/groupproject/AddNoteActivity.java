package com.example.groupproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNoteActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextDescription;
    private Button buttonSave;
    private DatabaseReference databaseReference;
    private String username;
    private static final String TAG = "AddNoteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSave = findViewById(R.id.buttonSave);

        // Get username from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("username", null);

        Log.d(TAG, "Username retrieved from SharedPreferences: " + username);

        if (username == null) {
            // Redirect to register if no user is logged in
            Intent intent = new Intent(AddNoteActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("notes");

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty()) {
            editTextTitle.setError("Title is required");
            editTextTitle.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            editTextDescription.setError("Description is required");
            editTextDescription.requestFocus();
            return;
        }

        String noteId = databaseReference.push().getKey();
        Note note = new Note(noteId, title, description);

        if (noteId != null) {
            databaseReference.child(noteId).setValue(note).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddNoteActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity and go back to the previous one
                } else {
                    Toast.makeText(AddNoteActivity.this, "Failed to add note", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
