package com.example.medicalmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalmanagementsystem.doctor.DoctorDashboardActivity;
import com.example.medicalmanagementsystem.patient.PatientDashboardActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        role = getIntent().getStringExtra("role");

        TextView tvRoleLabel = findViewById(R.id.tvRoleLabel);
        String roleDisplayName = "patient".equals(role) ? "Patient" : "Doctor";
        tvRoleLabel.setText(String.format("Login as %s", roleDisplayName));

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(v -> loginUser());
        findViewById(R.id.tvRegister).setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("role", role);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (mAuth.getCurrentUser() == null) return;
                String uid = mAuth.getCurrentUser().getUid();

                mDatabase.child("users").child(uid).get().addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        String userRole = snap.child("role").getValue(String.class);
                        if (userRole != null && userRole.equals(role)) {
                            Intent intent;
                            if ("patient".equals(role)) {
                                intent = new Intent(this, PatientDashboardActivity.class);
                            } else {
                                intent = new Intent(this, DoctorDashboardActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Wrong role selected", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    }
                });
            } else {
                String exceptionMsg = (task.getException() != null) ? task.getException().getMessage() : "Unknown Error";
                Toast.makeText(this, String.format("Login failed: %s", exceptionMsg), Toast.LENGTH_SHORT).show();
            }
        });
    }
}