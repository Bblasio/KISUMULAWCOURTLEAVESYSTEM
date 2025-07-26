package com.example.kisumulawcourtleavesystem;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EmployeeDashboardActivity extends AppCompatActivity {

    private TextView textViewWelcomeMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_dashboard);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("employees");

        textViewWelcomeMessage = findViewById(R.id.textViewWelcomeMessage);

        // Display welcome message with employee's name
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            databaseReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Employee employee = snapshot.getValue(Employee.class);
                            if (employee != null) {
                                String welcomeMessage = "Welcome, " + employee.getName() + "!";
                                textViewWelcomeMessage.setText(welcomeMessage);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }

        Button btnMyProfile = findViewById(R.id.btnMyProfile);
        btnMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmployeeProfileDialog();
            }
        });

        Button btnApplyLeave = findViewById(R.id.btnApplyLeave);
        btnApplyLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog("Apply Leave", "Are you sure you want to apply for leave?");
            }
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        Button btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPasswordDialog();
            }
        });
    }

    private void showEmployeeProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("My Profile");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            databaseReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Employee employee = snapshot.getValue(Employee.class);
                            if (employee != null) {
                                String userDetails = "Name: " + employee.getName() + "\n" +
                                        "ID Number: " + employee.getIdNumber() + "\n" +
                                        "Email: " + employee.getEmail() + "\n" +
                                        "Gender: " + employee.getGender() + "\n" +
                                        "Designation: " + employee.getDesignation() + "\n" +
                                        "Department: " + employee.getDepartment();

                                builder.setMessage(userDetails);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    } else {
                        builder.setMessage("Employee data not found");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                // Redirect to login activity after logout
                Intent intent = new Intent(EmployeeDashboardActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close current activity to prevent user from coming back to it with back button
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showConfirmationDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        // Add "My Leave" option with three buttons
        builder.setPositiveButton("Apply Leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLeaveApplicationDialog(); // Show leave application dialog
            }
        });
        builder.setNeutralButton("Review Leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLeaveRecordsDialog();
                // Add code to handle reviewing
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showLeaveApplicationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Application");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            databaseReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Employee employee = snapshot.getValue(Employee.class);
                            if (employee != null) {
                                String userDetails = "Name: " + employee.getName() + "\n" +
                                        "ID Number: " + employee.getIdNumber() + "\n" +
                                        "Email: " + employee.getEmail() + "\n" +
                                        "Gender: " + employee.getGender() + "\n" +
                                        "Designation: " + employee.getDesignation() + "\n" +
                                        "Department: " + employee.getDepartment();

                                builder.setMessage(userDetails);

                                View view = getLayoutInflater().inflate(R.layout.dialog_leave_application, null);
                                builder.setView(view);

                                EditText editTextLeaveStartDate = view.findViewById(R.id.editTextLeaveStartDate);
                                EditText editTextLeaveEndDate = view.findViewById(R.id.editTextLeaveEndDate);
                                EditText editTextLeaveDescription = view.findViewById(R.id.editTextLeaveDescription);

                                final Calendar calendar = Calendar.getInstance();

                                // Set up start date picker dialog
                                editTextLeaveStartDate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(EmployeeDashboardActivity.this,
                                                new DatePickerDialog.OnDateSetListener() {
                                                    @Override
                                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                        calendar.set(year, monthOfYear, dayOfMonth);
                                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                                        editTextLeaveStartDate.setText(sdf.format(calendar.getTime()));
                                                    }
                                                },
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                calendar.get(Calendar.DAY_OF_MONTH));

                                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                        datePickerDialog.show();
                                    }
                                });

                                // Set up end date picker dialog
                                editTextLeaveEndDate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(EmployeeDashboardActivity.this,
                                                new DatePickerDialog.OnDateSetListener() {
                                                    @Override
                                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                        calendar.set(year, monthOfYear, dayOfMonth);
                                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                                        editTextLeaveEndDate.setText(sdf.format(calendar.getTime()));
                                                    }
                                                },
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                calendar.get(Calendar.DAY_OF_MONTH));

                                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                        datePickerDialog.show();
                                    }
                                });

                                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String startDate = editTextLeaveStartDate.getText().toString();
                                        String endDate = editTextLeaveEndDate.getText().toString();
                                        String description = editTextLeaveDescription.getText().toString();

                                        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate) || TextUtils.isEmpty(description)) {
                                            Toast.makeText(EmployeeDashboardActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        DatabaseReference leaveRequestsRef = FirebaseDatabase.getInstance().getReference().child("leave_requests").push();
                                        LeaveRequest leaveRequest = new LeaveRequest(currentUser.getUid(), startDate, endDate, "pending", description);

                                        leaveRequestsRef.setValue(leaveRequest)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(EmployeeDashboardActivity.this, "Leave request submitted", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(EmployeeDashboardActivity.this, "Failed to submit leave request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EmployeeDashboardActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(EmployeeDashboardActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void showLeaveRecordsDialog() {
        // Fetch leave records for the current user from the database
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Assuming you have a unique user ID for each employee

            DatabaseReference leaveRequestsRef = FirebaseDatabase.getInstance().getReference().child("leave_requests");
            Query query = leaveRequestsRef.orderByChild("employeeId").equalTo(userId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> leaveList = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot leaveSnapshot : dataSnapshot.getChildren()) {
                            LeaveRequest leaveRequest = leaveSnapshot.getValue(LeaveRequest.class);
                            if (leaveRequest != null) {
                                // Add leave details to the list
                                leaveList.add("Start Date: " + leaveRequest.getStartDate() + "\nEnd Date: " + leaveRequest.getEndDate() + "\nStatus: " + leaveRequest.getStatus() + "\nDescription: " + leaveRequest.getDescription());
                            }
                        }
                    }

                    // Convert the list to an array for dialog display
                    String[] leaveArray = leaveList.toArray(new String[0]);

                    // Show dialog with leave records
                    AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeDashboardActivity.this);
                    builder.setTitle("Leave Records");
                    if (leaveArray.length > 0) {
                        builder.setItems(leaveArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Handle leave record selection if needed
                            }
                        });
                    } else {
                        builder.setMessage("No leave records found");
                    }
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    Toast.makeText(EmployeeDashboardActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Please input your email:");

        // Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString().trim();
                if (!email.isEmpty()) {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Password reset email sent successfully
                                        Toast.makeText(EmployeeDashboardActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();

                                        // Update user's email in the Firebase Authentication
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if (currentUser != null) {
                                            currentUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Email updated successfully, update employee record in the database
                                                        DatabaseReference employeeRef = (DatabaseReference) databaseReference.orderByChild("email").equalTo(email);
                                                        employeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                        Employee employee = snapshot.getValue(Employee.class);
                                                                        if (employee != null) {
                                                                            // Update employee details
                                                                            employee.setEmail(email);
                                                                            // Assuming you want to update the password too
                                                                            // You may need to prompt the user for a new password
                                                                            // and update it accordingly.
                                                                            // For example:
                                                                            // currentUser.updatePassword(newPassword);

                                                                            // Update employee record in the database
                                                                            snapshot.getRef().setValue(employee)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                // Employee details updated successfully
                                                                                            } else {
                                                                                                // Failed to update employee details
                                                                                                Toast.makeText(EmployeeDashboardActivity.this, "Failed to update employee details in the database.", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                // Handle database error
                                                            }
                                                        });
                                                    } else {
                                                        // Failed to update email
                                                        Toast.makeText(EmployeeDashboardActivity.this, "Failed to update email. Please try again later.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        // Failed to send password reset email
                                        Toast.makeText(EmployeeDashboardActivity.this, "Failed to send reset email. Please try again later.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(EmployeeDashboardActivity.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
