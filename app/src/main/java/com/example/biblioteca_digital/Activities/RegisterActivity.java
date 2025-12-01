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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText nombreEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView backToLoginLink;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nombreEditText = findViewById(R.id.nombreEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        backToLoginLink = findViewById(R.id.backToLoginLink);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(v -> registrarUsuario());
        backToLoginLink.setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        String nombre = nombreEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (nombre.isEmpty()) {
            nombreEditText.setError("Ingresa tu nombre");
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Ingresa tu email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email inválido");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Ingresa una contraseña");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Al menos 6 caracteres");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Las contraseñas no coinciden");
            return;
        }

        Toast.makeText(this, "Creando cuenta...", Toast.LENGTH_SHORT).show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String userId = auth.getCurrentUser().getUid();

                    // Guardar el nombre en Firestore
                    HashMap<String, Object> datos = new HashMap<>();
                    datos.put("nombre", nombre);
                    datos.put("email", email);

                    firestore.collection("usuarios")
                            .document(userId)
                            .set(datos)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}