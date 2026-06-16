package com.example.medicalmanagementsystem.doctor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicalmanagementsystem.R;
import com.example.medicalmanagementsystem.model.Appointment;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.VH> {
    private final List<Appointment> list;
    private final boolean showActions;

    public AppointmentAdapter(List<Appointment> list, boolean showActions) {
        this.list = list;
        this.showActions = showActions;
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
        h.llActions.setVisibility(showActions ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        public TextView tvPatientName, tvDoctorName, tvDateTime, tvStatus, tvSymptoms;
        public View llActions;

        public VH(@NonNull View v) {
            super(v);
            tvPatientName = v.findViewById(R.id.tvPatientName);
            tvDoctorName = v.findViewById(R.id.tvDoctorName);
            tvDateTime = v.findViewById(R.id.tvDateTime);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvSymptoms = v.findViewById(R.id.tvSymptoms);
            llActions = v.findViewById(R.id.llActions);
        }
    }
}