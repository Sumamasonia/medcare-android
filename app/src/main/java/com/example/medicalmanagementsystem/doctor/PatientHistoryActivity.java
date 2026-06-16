package com.example.medicalmanagementsystem.doctor;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicalmanagementsystem.R;
import com.example.medicalmanagementsystem.model.Treatment;
import com.example.medicalmanagementsystem.patient.TreatmentHistoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PatientHistoryActivity extends AppCompatActivity {
    private final List<Treatment> list = new ArrayList<>();
    private TreatmentHistoryActivity.TreatmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Patient Histories");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.pat_history);
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreatmentHistoryActivity.TreatmentAdapter(list);
        rv.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        String doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("treatments")
                .orderByChild("doctorId").equalTo(doctorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    @SuppressWarnings("NotifyDataSetChanged")
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Treatment t = snap.getValue(Treatment.class);
                            if (t != null) list.add(t);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {}
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}