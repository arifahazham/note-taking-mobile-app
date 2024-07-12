package com.example.groupproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewNoteActivity extends AppCompatActivity {
    private TextView textViewTitle, textViewDescription;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);

        note = (Note) getIntent().getSerializableExtra("note");

        if (note != null) {
            textViewTitle.setText(note.getTitle());
            textViewDescription.setText(note.getDescription());
        }
    }
}
