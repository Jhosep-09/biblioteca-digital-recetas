package com.example.biblioteca_digital.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.biblioteca_digital.MainActivity;
import com.example.biblioteca_digital.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerLink;

    private FirebaseAuth auth; // Firebase Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);

        auth = FirebaseAuth.getInstance(); // Inicializar Auth

        loginButton.setOnClickListener(v -> iniciarSesion());

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void iniciarSesion() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Por favor ingresa tu email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email no válido");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Ingresa tu contraseña");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Ingresando...");

        // 🔥 Iniciar sesión con Firebase
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loginButton.setEnabled(true);
                    loginButton.setText("Iniciar sesión");

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Bienvenido nuevamente", Toast.LENGTH_SHORT).show();
                        irAMain();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void irAMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}