package com.example.medicalmanagementsystem.doctor;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalmanagementsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Reuse the patient profile layout (it has same fields)
        setContentView(R.layout.activity_patient_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Doctor Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .get().addOnSuccessListener(snap -> {
                    if (snap.exists()) {
                        String name = snap.child("name").getValue(String.class);
                        String spec = snap.child("specialization").getValue(String.class);
                        ((TextView) findViewById(R.id.tvName)).setText(String.format("Dr. %s", name));
                        ((TextView) findViewById(R.id.tvEmail)).setText(snap.child("email").getValue(String.class));
                        ((TextView) findViewById(R.id.tvPhone)).setText(snap.child("phone").getValue(String.class));
                        ((TextView) findViewById(R.id.tvAge)).setText(spec != null ? spec : "--");
                        ((TextView) findViewById(R.id.tvGender)).setText(snap.child("gender").getValue(String.class));
                        ((TextView) findViewById(R.id.tvAddress)).setText(snap.child("address").getValue(String.class));
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}