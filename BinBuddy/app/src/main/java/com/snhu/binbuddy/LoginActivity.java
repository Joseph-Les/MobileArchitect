package com.snhu.binbuddy;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail; // Input field for email
    private EditText editTextPassword; // Input field for password
    private DatabaseHelper databaseHelper; // Helper class for database operations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initializing the database helper and UI elements
        databaseHelper = new DatabaseHelper(this);
        editTextEmail = findViewById(R.id.usernameEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button createAccountButton = findViewById(R.id.createAccountButton);

        // Setting click listeners for buttons
        loginButton.setOnClickListener(view -> login());
        createAccountButton.setOnClickListener(view -> createAccount());
    }

    private void login() {
        // Fetch user input and attempt login
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (validateCredentials(email, password) && databaseHelper.checkUser(email, password)) {
            // Display success and navigate to main activity
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        } else {
            // Display failure message
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccount() {
        // Validate input and create new account
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            // Validate non-empty fields
            Toast.makeText(this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!databaseHelper.checkUserExists(email)) {
            // Add user to database if not existing
            databaseHelper.addUser(email, password);
            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
        } else {
            // Notify about existing account
            Toast.makeText(this, "Account with this email already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMainActivity() {
        // Start main activity and close login activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validateCredentials(String email, String password) {
        // Simple validation for non-empty credentials
        return !email.isEmpty() && !password.isEmpty();
    }
}
