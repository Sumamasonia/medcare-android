package com.example.medicalmanagementsystem.model;

@SuppressWarnings("unused")
public class User {
    public String uid, name, email, phone, role, age, gender, specialization, address;

    public User() {}

    public User(String uid, String name, String email, String phone,
                String role, String age, String gender, String specialization, String address) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.age = age;
        this.gender = gender;
        this.specialization = specialization;
        this.address = address;
    }
}
