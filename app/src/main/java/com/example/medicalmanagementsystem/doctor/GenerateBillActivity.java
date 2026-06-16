package com.example.medicalmanagementsystem.doctor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalmanagementsystem.R;
import com.example.medicalmanagementsystem.model.Bill;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class GenerateBillActivity extends AppCompatActivity {
    private TextInputEditText etConsultation, etMedicine;
    private TextView tvTotal;
    private String appointmentId, patientId, patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_bill);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Generate Bill");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        appointmentId = getIntent().getStringExtra("appointmentId");
        patientId = getIntent().getStringExtra("patientId");
        patientName = getIntent().getStringExtra("patientName");

        TextView tvPatientInfo = findViewById(R.id.tvPatientInfo);
        tvPatientInfo.setText(String.format("Patient: %s", patientName));

        etConsultation = findViewById(R.id.etConsultationFee);
        etMedicine = findViewById(R.id.etMedicineFee);
        tvTotal = findViewById(R.id.tvTotal);

        TextWatcher tw = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { updateTotal(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etConsultation.addTextChangedListener(tw);
        etMedicine.addTextChangedListener(tw);

        findViewById(R.id.btnGenerateBill).setOnClickListener(v -> generateBill());
    }

    private void updateTotal() {
        try {
            String consText = etConsultation.getText() != null ? etConsultation.getText().toString() : "0";
            String medText = etMedicine.getText() != null ? etMedicine.getText().toString() : "0";
            double c = consText.isEmpty() ? 0 : Double.parseDouble(consText);
            double m = medText.isEmpty() ? 0 : Double.parseDouble(medText);
            tvTotal.setText(String.format(Locale.ROOT, "Total: Rs. %.2f", (c + m)));
        } catch (NumberFormatException e) {
            tvTotal.setText(R.string.default_total);
        }
    }

    private void generateBill() {
        String consStr = etConsultation.getText() != null ? etConsultation.getText().toString().trim() : "";
        String medStr = etMedicine.getText() != null ? etMedicine.getText().toString().trim() : "";
        if (consStr.isEmpty()) {
            Toast.makeText(this, "Enter consultation fee", Toast.LENGTH_SHORT).show();
            return;
        }

        double cons = Double.parseDouble(consStr);
        double med = medStr.isEmpty() ? 0 : Double.parseDouble(medStr);
        double total = cons + med;

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.child("users").child(doctorId).get().addOnSuccessListener(snap -> {
            String doctorName = String.format("Dr. %s", snap.child("name").getValue(String.class));
            String generatedKey = db.child("bills").push().getKey();
            String billId = generatedKey != null ? generatedKey : UUID.randomUUID().toString();
            String date = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new Date());

            Bill bill = new Bill();
            bill.billId = billId;
            bill.appointmentId = appointmentId;
            bill.patientId = patientId;
            bill.doctorId = doctorId;
            bill.patientName = patientName;
            bill.doctorName = doctorName;
            bill.date = date;
            bill.consultationFee = cons;
            bill.medicineFee = med;
            bill.totalAmount = total;
            bill.status = "unpaid";

            db.child("bills").child(billId).setValue(bill).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, String.format(Locale.ROOT, "Bill generated: Rs. %.2f", total), Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        });
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}