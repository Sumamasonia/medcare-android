package com.example.medicalmanagementsystem.model;

public class Bill {
    public String billId, appointmentId, patientId, doctorId;
    public String patientName, doctorName, date;
    public double consultationFee, medicineFee, totalAmount;
    public String status; // paid/unpaid

    public Bill() {}
}
