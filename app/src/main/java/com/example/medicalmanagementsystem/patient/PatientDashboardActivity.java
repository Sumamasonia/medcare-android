package com.example.medicalmanagementsystem.patient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalmanagementsystem.LoginActivity;
import com.example.medicalmanagementsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientDashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        String uid = mAuth.getCurrentUser().getUid();

        mDatabase.child("users").child(uid).get().addOnSuccessListener(snap -> {
            if (snap.exists()) {
                String name = snap.child("name").getValue(String.class);
                TextView tvWelcome = findViewById(R.id.tvWelcome);
                tvWelcome.setText(String.format("Welcome, %s", name));
            }
        });

        findViewById(R.id.cardProfile).setOnClickListener(v ->
                startActivity(new Intent(this, PatientProfileActivity.class)));

        findViewById(R.id.cardBookAppointment).setOnClickListener(v ->
                startActivity(new Intent(this, BookAppointmentActivity.class)));

        findViewById(R.id.cardBillHistory).setOnClickListener(v ->
                startActivity(new Intent(this, BillHistoryActivity.class)));

        findViewById(R.id.cardTreatmentHistory).setOnClickListener(v ->
                startActivity(new Intent(this, TreatmentHistoryActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class)
                    .putExtra("role", "patient")
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
    }
}