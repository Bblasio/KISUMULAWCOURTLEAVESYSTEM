package com.example.kisumulawcourtleavesystem;

public class UserRecord {
    private String uid;
    private String email;
    private String name;
    // Add more fields as needed

    public UserRecord() {
        // Default constructor required for Firebase
    }

    public UserRecord(String uid, String email, String name) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        // Initialize other fields as needed
    }

    // Getters and setters for the fields

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
