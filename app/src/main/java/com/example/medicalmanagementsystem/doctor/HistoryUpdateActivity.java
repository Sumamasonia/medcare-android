package com.example.medicalmanagementsystem.doctor;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalmanagementsystem.R;
import com.example.medicalmanagementsystem.model.Treatment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class HistoryUpdateActivity extends AppCompatActivity {
    private TextInputEditText etDisease, etPrescription, etProgress;
    private String appointmentId, patientId, patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_update);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Treatment");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        appointmentId = getIntent().getStringExtra("appointmentId");
        patientId = getIntent().getStringExtra("patientId");
        patientName = getIntent().getStringExtra("patientName");

        ((TextView) findViewById(R.id.tvPatientName)).setText(String.format("Patient: %s", patientName));

        etDisease = findViewById(R.id.etDisease);
        etPrescription = findViewById(R.id.etPrescription);
        etProgress = findViewById(R.id.etProgress);

        findViewById(R.id.btnSave).setOnClickListener(v -> saveTreatment());
    }

    private void saveTreatment() {
        String disease = etDisease.getText() != null ? etDisease.getText().toString().trim() : "";
        String prescription = etPrescription.getText() != null ? etPrescription.getText().toString().trim() : "";
        String progress = etProgress.getText() != null ? etProgress.getText().toString().trim() : "";

        if (disease.isEmpty() || prescription.isEmpty()) {
            Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String generatedKey = db.child("treatments").push().getKey();
        String treatmentId = generatedKey != null ? generatedKey : UUID.randomUUID().toString();
        String date = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new Date());

        db.child("users").child(doctorId).get().addOnSuccessListener(snap -> {
            String doctorName = String.format("Dr. %s", snap.child("name").getValue(String.class));
            Treatment t = new Treatment();
            t.treatmentId = treatmentId;
            t.appointmentId = appointmentId;
            t.patientId = patientId;
            t.doctorId = doctorId;
            t.patientName = patientName;
            t.doctorName = doctorName;
            t.disease = disease;
            t.prescription = prescription;
            t.progress = progress;
            t.date = date;

            db.child("treatments").child(treatmentId).setValue(t).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Mark appointment as completed
                    db.child("appointments").child(appointmentId).child("status").setValue("completed");
                    Toast.makeText(this, "Treatment saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}