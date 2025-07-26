package com.example.kisumulawcourtleavesystem;

public class Employee {
    private String id;
    private String name;
    private String idNumber;
    private String email;
    private String gender;
    private String designation;
    private String department;

    // Constructors, getters, and setters
    public Employee() {
        // Default constructor required for Firebase
    }

    public Employee(String id, String name, String idNumber, String email, String gender, String designation, String department) {
        this.id = id;
        this.name = name;
        this.idNumber = idNumber;
        this.email = email;
        this.gender = gender;
        this.designation = designation;
        this.department = department;
    }


    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
