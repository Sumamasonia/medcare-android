package com.example.medicalmanagementsystem.model;

@SuppressWarnings("unused")
public class Appointment {
    public String appointmentId, patientId, doctorId, patientName, doctorName;
    public String date, time, status, symptoms, notes;
    // status: pending, accepted, rejected, completed

    public Appointment() {}

    public Appointment(String appointmentId, String patientId, String doctorId,
                       String patientName, String doctorName, String date,
                       String time, String status, String symptoms) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.status = status;
        this.symptoms = symptoms;
    }
}
