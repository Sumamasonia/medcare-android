package com.example.medicalmanagementsystem.doctor;

import android.content.Intent;
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
import com.example.medicalmanagementsystem.model.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodaysAppointmentsActivity extends AppCompatActivity {
    private final List<Appointment> list = new ArrayList<>();
    private TodayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.todays);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.todays);
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodayAdapter(list, this);
        rv.setAdapter(adapter);

        String today = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new Date());
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        String doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("appointments")
                .orderByChild("doctorId").equalTo(doctorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    @SuppressWarnings("NotifyDataSetChanged")
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Appointment a = snap.getValue(Appointment.class);
                            if (a != null && today.equals(a.date)) list.add(a);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {}
                });
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }

    public static class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.VH> {
        private final List<Appointment> list;
        private final TodaysAppointmentsActivity ctx;

        TodayAdapter(List<Appointment> list, TodaysAppointmentsActivity ctx) {
            this.list = list; this.ctx = ctx;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Appointment a = list.get(pos);
            h.tvPatientName.setText(a.patientName);
            h.tvDoctorName.setText(a.doctorName);
            h.tvDateTime.setText(String.format("%s | %s", a.date, a.time));
            h.tvStatus.setText(a.status.toUpperCase(Locale.ROOT));
            h.tvSymptoms.setText(String.format("Symptoms: %s", a.symptoms));

            if ("accepted".equals(a.status)) {
                h.llActions.setVisibility(View.GONE);
                h.tvStatus.setBackgroundColor(0xFF2E7D32);
                h.itemView.setOnClickListener(v -> showOptions(a));
            } else if ("pending".equals(a.status)) {
                h.llActions.setVisibility(View.VISIBLE);
                h.btnAccept.setOnClickListener(v -> updateStatus(a, "accepted"));
                h.btnReject.setOnClickListener(v -> updateStatus(a, "rejected"));
            } else {
                h.llActions.setVisibility(View.GONE);
            }
        }

        private void showOptions(Appointment a) {
            String[] options = {"Update Treatment", "Generate Bill"};
            new android.app.AlertDialog.Builder(ctx)
                    .setTitle(String.format("Actions for %s", a.patientName))
                    .setItems(options, (d, which) -> {
                        Intent intent;
                        if (which == 0) {
                            intent = new Intent(ctx, HistoryUpdateActivity.class);
                        } else {
                            intent = new Intent(ctx, GenerateBillActivity.class);
                        }
                        intent.putExtra("appointmentId", a.appointmentId);
                        intent.putExtra("patientId", a.patientId);
                        intent.putExtra("patientName", a.patientName);
                        ctx.startActivity(intent);
                    }).show();
        }

        @SuppressWarnings("NotifyDataSetChanged")
        private void updateStatus(Appointment a, String newStatus) {
            FirebaseDatabase.getInstance().getReference("appointments")
                    .child(a.appointmentId).child("status").setValue(newStatus);
            a.status = newStatus;
            notifyDataSetChanged();
        }

        @Override public int getItemCount() { return list.size(); }

        public static class VH extends RecyclerView.ViewHolder {
            TextView tvPatientName, tvDoctorName, tvDateTime, tvStatus, tvSymptoms;
            View llActions;
            android.widget.Button btnAccept, btnReject;

            public VH(@NonNull View v) {
                super(v);
                tvPatientName = v.findViewById(R.id.tvPatientName);
                tvDoctorName = v.findViewById(R.id.tvDoctorName);
                tvDateTime = v.findViewById(R.id.tvDateTime);
                tvStatus = v.findViewById(R.id.tvStatus);
                tvSymptoms = v.findViewById(R.id.tvSymptoms);
                llActions = v.findViewById(R.id.llActions);
                btnAccept = v.findViewById(R.id.btnAccept);
                btnReject = v.findViewById(R.id.btnReject);
            }
        }
    }
}