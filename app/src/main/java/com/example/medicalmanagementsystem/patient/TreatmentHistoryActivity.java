package com.example.medicalmanagementsystem.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicalmanagementsystem.R;
import com.example.medicalmanagementsystem.model.Treatment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class TreatmentHistoryActivity extends AppCompatActivity {

    private final List<Treatment> treatments = new ArrayList<>();
    private TreatmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Treatment History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.treatment_history);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreatmentAdapter(treatments);
        rv.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("treatments")
                .orderByChild("patientId")
                .equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    @SuppressWarnings("NotifyDataSetChanged")
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        treatments.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Treatment t = snap.getValue(Treatment.class);
                            if (t != null) treatments.add(t);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // handle if needed
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static class TreatmentAdapter extends RecyclerView.Adapter<TreatmentAdapter.VH> {

        private final List<Treatment> list;

        public TreatmentAdapter(List<Treatment> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_treatment, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Treatment t = list.get(pos);
            h.tvDoctorName.setText(t.doctorName);
            h.tvDate.setText(t.date);
            h.tvDisease.setText(h.itemView.getContext()
                    .getString(R.string.label_disease, t.disease));
            h.tvPrescription.setText(h.itemView.getContext()
                    .getString(R.string.label_prescription, t.prescription));
            h.tvProgress.setText(h.itemView.getContext()
                    .getString(R.string.label_progress, t.progress));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class VH extends RecyclerView.ViewHolder {
            public TextView tvDoctorName, tvDate, tvDisease, tvPrescription, tvProgress;

            VH(View v) {
                super(v);
                tvDoctorName = v.findViewById(R.id.tvDoctorName);
                tvDate = v.findViewById(R.id.tvDate);
                tvDisease = v.findViewById(R.id.tvDisease);
                tvPrescription = v.findViewById(R.id.tvPrescription);
                tvProgress = v.findViewById(R.id.tvProgress);
            }
        }
    }
}