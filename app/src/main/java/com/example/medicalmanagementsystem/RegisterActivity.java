package com.example.medicalmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalmanagementsystem.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etName, etEmail, etPhone, etAge, etGender,
            etSpecialization, etAddress, etPassword;
    private FirebaseAuth mAuth;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        role = getIntent().getStringExtra("role");

        TextView tvRoleLabel = findViewById(R.id.tvRoleLabel);
        String roleDisplayName = "patient".equals(role) ? "Patient" : "Doctor";
        tvRoleLabel.setText(String.format("Register as %s", roleDisplayName));

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAge = findViewById(R.id.etAge);
        etGender = findViewById(R.id.etGender);
        etSpecialization = findViewById(R.id.etSpecialization);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        TextInputLayout tilSpecialization = findViewById(R.id.tilSpecialization);

        if ("doctor".equals(role)) {
            tilSpecialization.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btnRegister).setOnClickListener(v -> registerUser());
        findViewById(R.id.tvLogin).setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String age = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String gender = etGender.getText() != null ? etGender.getText().toString().trim() : "";
        String specialization = etSpecialization.getText() != null ? etSpecialization.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Name, Email and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference localDb = FirebaseDatabase.getInstance().getReference();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (mAuth.getCurrentUser() == null) return;
                String uid = mAuth.getCurrentUser().getUid();

                User user = new User(uid, name, email, phone, role, age, gender, specialization, address);
                localDb.child("users").child(uid).setValue(user).addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.putExtra("role", role);
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                String exceptionMsg = (task.getException() != null) ? task.getException().getMessage() : "Unknown Error";
                Toast.makeText(this, String.format("Registration failed: %s", exceptionMsg), Toast.LENGTH_SHORT).show();
            }
        });
    }
}