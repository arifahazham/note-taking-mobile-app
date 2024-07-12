package com.example.groupproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity1 extends AppCompatActivity {

    private static final String TAG = "LoginActivity1";
    private EditText loginUsername, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() || !validatePassword()) {
                    return;
                } else {
                    loginUser();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity1.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    private void loginUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        // Fetch email associated with the username
        reference = FirebaseDatabase.getInstance().getReference("users");
        Query query = reference.orderByChild("username").equalTo(userUsername);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String emailFromDB = userSnapshot.child("email").getValue(String.class);
                        if (emailFromDB != null) {
                            signInWithEmail(emailFromDB, userPassword);
                        } else {
                            Toast.makeText(LoginActivity1.this, "Error: Email not found for this username", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity1.this, "Username not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: ", error.toException());
                Toast.makeText(LoginActivity1.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        fetchUserData(user);
                    } else {
                        // Sign-in failed
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity1.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchUserData(FirebaseUser user) {
        if (user != null) {
            String uid = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String usernameFromDB = snapshot.child("username").getValue(String.class);
                        String emailFromDB = snapshot.child("email").getValue(String.class);

                        // Save username in SharedPreferences
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity1.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", usernameFromDB);
                        editor.apply();

                        Log.d(TAG, "Username saved in SharedPreferences: " + usernameFromDB);

                        Intent intent = new Intent(LoginActivity1.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close LoginActivity1
                    } else {
                        Log.w(TAG, "User data not found");
                        Toast.makeText(LoginActivity1.this, "User data not found", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Database error: ", error.toException());
                }
            });
        }
    }
}
