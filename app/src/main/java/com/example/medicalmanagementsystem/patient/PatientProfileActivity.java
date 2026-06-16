package com.example.medicalmanagementsystem.patient;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalmanagementsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid);

        ref.get().addOnSuccessListener(snap -> {
            if (snap.exists()) {
                ((TextView) findViewById(R.id.tvName)).setText(
                        snap.child("name").getValue(String.class));
                ((TextView) findViewById(R.id.tvEmail)).setText(
                        snap.child("email").getValue(String.class));
                ((TextView) findViewById(R.id.tvPhone)).setText(
                        snap.child("phone").getValue(String.class));
                ((TextView) findViewById(R.id.tvAge)).setText(
                        snap.child("age").getValue(String.class));
                ((TextView) findViewById(R.id.tvGender)).setText(
                        snap.child("gender").getValue(String.class));
                ((TextView) findViewById(R.id.tvAddress)).setText(
                        snap.child("address").getValue(String.class));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}