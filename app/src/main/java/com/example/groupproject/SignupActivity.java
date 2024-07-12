package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText signupUsername, signupEmail, signupPassword;
    private TextView loginRedirectText;
    private Button signupButton;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupUsername = findViewById(R.id.signup_username);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        mAuth = FirebaseAuth.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signupEmail.getText().toString().trim();
                String username = signupUsername.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkUsernameExists(username, email, password);
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity1.class);
                startActivity(intent);
            }
        });
    }

    private void checkUsernameExists(String username, String email, String password) {
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username already exists
                    Toast.makeText(SignupActivity.this, "Username already exists. Please choose a different username.", Toast.LENGTH_LONG).show();
                } else {
                    // Username does not exist, proceed with registration
                    registerUser(email, username, password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "checkUsernameExists:onCancelled", databaseError.toException());
                Toast.makeText(SignupActivity.this, "Failed to check username: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerUser(String email, String username, String password) {
        Log.d(TAG, "Attempting to register user with email: " + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration success
                        Log.d(TAG, "Registration successful");
                        FirebaseUser user = mAuth.getCurrentUser();
                        storeUserData(user, email, username);
                    } else {
                        // Registration failed
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignupActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void storeUserData(FirebaseUser user, String email, String username) {
        String uid = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");
        HelperClass helperClass = new HelperClass(email, username, uid);

        reference.child(uid).setValue(helperClass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User data stored successfully");
                        Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        finish(); // Finish current activity
                    } else {
                        Log.w(TAG, "storeUserData:failure", task.getException());
                        Toast.makeText(SignupActivity.this, "Failed to store user data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
