package com.example.medicalmanagementsystem.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalmanagementsystem.LoginActivity;
import com.example.medicalmanagementsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorDashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

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
                ((TextView) findViewById(R.id.tvWelcome)).setText(String.format("Dr. %s", name));
            }
        });

        findViewById(R.id.cardProfile).setOnClickListener(v ->
                startActivity(new Intent(this, DoctorProfileActivity.class)));

        findViewById(R.id.cardPending).setOnClickListener(v ->
                startActivity(new Intent(this, PendingAppointmentsActivity.class)));

        findViewById(R.id.cardToday).setOnClickListener(v ->
                startActivity(new Intent(this, TodaysAppointmentsActivity.class)));

        findViewById(R.id.cardHistory).setOnClickListener(v ->
                startActivity(new Intent(this, PatientHistoryActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class)
                    .putExtra("role", "doctor")
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
    }
}