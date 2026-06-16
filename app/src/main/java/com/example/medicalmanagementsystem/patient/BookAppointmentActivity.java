package com.example.medicalmanagementsystem.patient;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicalmanagementsystem.R;
import com.example.medicalmanagementsystem.model.Appointment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BookAppointmentActivity extends AppCompatActivity {
    private Spinner spinnerDoctor;
    private TextInputEditText etDate, etTime, etSymptoms;
    private DatabaseReference mDatabase;
    private final List<String> doctorNames = new ArrayList<>();
    private final List<String> doctorIds = new ArrayList<>();
    private String currentPatientName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Book Appointment");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        spinnerDoctor = findViewById(R.id.spinnerDoctor);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etSymptoms = findViewById(R.id.etSymptoms);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("users").child(uid).get().addOnSuccessListener(snap -> {
            if (snap.exists()) currentPatientName = snap.child("name").getValue(String.class);
        });

        loadDoctors();

        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) ->
                    etDate.setText(String.format(Locale.ROOT, "%d/%d/%d", d, m + 1, y)),
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        etTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(this, (view, h, min) ->
                    etTime.setText(String.format(Locale.ROOT, "%02d:%02d", h, min)),
                    cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
        });

        findViewById(R.id.btnBook).setOnClickListener(v -> bookAppointment());
    }

    private void loadDoctors() {
        mDatabase.child("users").orderByChild("role").equalTo("doctor")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        doctorNames.clear(); doctorIds.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String name = snap.child("name").getValue(String.class);
                            String spec = snap.child("specialization").getValue(String.class);
                            doctorNames.add(String.format("Dr. %s - %s", name, spec));
                            doctorIds.add(snap.getKey());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(BookAppointmentActivity.this,
                                android.R.layout.simple_spinner_item, doctorNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDoctor.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {}
                });
    }

    private void bookAppointment() {
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String time = etTime.getText() != null ? etTime.getText().toString().trim() : "";
        String symptoms = etSymptoms.getText() != null ? etSymptoms.getText().toString().trim() : "";
        int selectedIndex = spinnerDoctor.getSelectedItemPosition();

        if (date.isEmpty() || time.isEmpty() || symptoms.isEmpty() || doctorIds.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String doctorId = doctorIds.get(selectedIndex);
        String doctorName = doctorNames.get(selectedIndex);

        String generatedKey = mDatabase.child("appointments").push().getKey();
        String appointmentId = generatedKey != null ? generatedKey : UUID.randomUUID().toString();

        Appointment appt = new Appointment(appointmentId, patientId, doctorId,
                currentPatientName, doctorName, date, time, "pending", symptoms);

        mDatabase.child("appointments").child(appointmentId).setValue(appt)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to book", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}