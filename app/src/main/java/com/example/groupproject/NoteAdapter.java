package com.example.groupproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Random;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context context;
    private List<Note> noteList;
    private int[] colors;

    public NoteAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
        colors = new int[]{
                R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4,
                R.color.color5,
                R.color.color6,
                R.color.color7,
                R.color.color8,
                R.color.color9,
                R.color.color10
        };
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.descriptionTextView.setText(note.getDescription());

        // Set a random background color
        Random random = new Random();
        int color = colors[random.nextInt(colors.length)];
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, color));

        // Set click listener to open ViewNoteActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewNoteActivity.class);
            intent.putExtra("note", note);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String username = preferences.getString("username", null);
            Log.d("NoteAdapter", "Note ID: " + note.getNoteId() + ", Username: " + username);
            intent.putExtra("username", username);
            context.startActivity(intent);
        });

        // Set click listener for menu button
        holder.menuButton.setOnClickListener(v -> showPopupMenu(v, note));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    private void showPopupMenu(View view, Note note) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.note_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_view) {
                viewNote(note);
                return true;
            } else if (item.getItemId() == R.id.menu_edit) {
                editNote(note);
                return true;
            } else if (item.getItemId() == R.id.menu_delete) {
                deleteNote(note);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void viewNote(Note note) {
        Intent intent = new Intent(context, ViewNoteActivity.class);
        intent.putExtra("note", note);
        context.startActivity(intent);
    }

    private void editNote(Note note) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra("note", note);
        context.startActivity(intent);
    }


    private void deleteNote(Note note) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = preferences.getString("username", null);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(username).child("notes").child(note.getNoteId());
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                // Remove the note from the list and notify the adapter
                noteList.remove(note);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Failed to delete note", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageButton menuButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            menuButton = itemView.findViewById(R.id.imageButtonMenu);
        }
    }
}
