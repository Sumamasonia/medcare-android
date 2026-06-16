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
import com.example.medicalmanagementsystem.model.Bill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BillHistoryActivity extends AppCompatActivity {
    private final List<Bill> bills = new ArrayList<>();
    private BillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.menu_bill_history);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.menu_bill_history);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BillAdapter(bills);
        recyclerView.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("bills")
                .orderByChild("patientId").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    @SuppressWarnings("NotifyDataSetChanged")
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bills.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Bill b = snap.getValue(Bill.class);
                            if (b != null) bills.add(b);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {}
                });
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }

    public static class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillVH> {
        private final List<Bill> bills;

        public BillAdapter(List<Bill> bills) { this.bills = bills; }

        @NonNull
        @Override
        public BillVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
            return new BillVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull BillVH holder, int pos) {
            Bill b = bills.get(pos);
            holder.tvDoctorName.setText(b.doctorName);
            holder.tvDate.setText(String.format("Date: %s", b.date));
            holder.tvConsultation.setText(String.format(Locale.ROOT, "Consultation: Rs. %.2f", b.consultationFee));
            holder.tvMedicine.setText(String.format(Locale.ROOT, "Medicine: Rs. %.2f", b.medicineFee));
            holder.tvTotal.setText(String.format(Locale.ROOT, "Rs. %.2f", b.totalAmount));
        }

        @Override public int getItemCount() { return bills.size(); }

        public static class BillVH extends RecyclerView.ViewHolder {
            public TextView tvDoctorName, tvDate, tvConsultation, tvMedicine, tvTotal;

            public BillVH(@NonNull View v) {
                super(v);
                tvDoctorName = v.findViewById(R.id.tvDoctorName);
                tvDate = v.findViewById(R.id.tvDate);
                tvConsultation = v.findViewById(R.id.tvConsultation);
                tvMedicine = v.findViewById(R.id.tvMedicine);
                tvTotal = v.findViewById(R.id.tvTotal);
            }
        }
    }
}