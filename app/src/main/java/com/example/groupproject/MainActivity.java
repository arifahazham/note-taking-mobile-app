package com.example.groupproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.groupproject.RecyclerView.SpacesItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewNotes;
    private FloatingActionButton fabAddNote;
    private List<Note> noteList;
    private NoteAdapter noteAdapter;
    private DatabaseReference databaseReference;
    private String username;
    private static final String TAG = "MainActivity";
    private ImageButton imageButtonMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get username from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString("username", null);

        Log.d(TAG, "Username retrieved from SharedPreferences: " + username);

        if (username == null) {
            // Redirect to register if no user is logged in
            Intent intent = new Intent(MainActivity.this, LoginActivity1.class);
            startActivity(intent);
            finish();
            return;
        }

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);

        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)); // 2 columns
        recyclerViewNotes.addItemDecoration(new SpacesItemDecoration(spacingInPixels)); // Apply item decoration
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(this, noteList);
        recyclerViewNotes.setAdapter(noteAdapter);

        fabAddNote = findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("notes");

        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    noteList.add(note);
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read notes", databaseError.toException());
            }
        });

        imageButtonMenu = findViewById(R.id.imageButtonMenu);
        imageButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_logout) {
                    // Handle logout action
                    logoutUser();
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void logoutUser() {
        // Clear username from SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().remove("username").apply();

        // Redirect to login screen
        Intent intent = new Intent(MainActivity.this, LoginActivity1.class);
        startActivity(intent);
        finish();
    }

    public String getUsername() {
        return username;
    }
}
