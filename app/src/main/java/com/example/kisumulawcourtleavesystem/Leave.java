package com.example.kisumulawcourtleavesystem;

public class Leave {
    private String leaveType;
    private String status;
    private String startDate;
    private String endDate;
    private String employeeId;
    private String description; // New field for leave description

    public Leave() {
        // Default constructor required for Firebase
    }

    public Leave(String leaveType, String status) {
        this.leaveType = leaveType;
        this.status = status;
    }

    // Constructor with additional fields for leave records
    public Leave(String leaveType, String status, String startDate, String endDate, String employeeId, String description) {
        this.leaveType = leaveType;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employeeId = employeeId;
        this.description = description;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
