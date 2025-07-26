package com.example.kisumulawcourtleavesystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminOperationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_operation);

        mAuth = FirebaseAuth.getInstance();

        Button buttonLeave = findViewById(R.id.buttonLeave);
        Button buttonEmployees = findViewById(R.id.buttonemployees);
        Button buttonLogout = findViewById(R.id.buttonlogout);
        Button buttonChange = findViewById(R.id.buttonchange);

        buttonLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch all leave requests from the database
                DatabaseReference leaveRequestsRef = FirebaseDatabase.getInstance().getReference().child("leave_requests");
                leaveRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> leaveList = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot leaveSnapshot : dataSnapshot.getChildren()) {
                                LeaveRequest leaveRequest = leaveSnapshot.getValue(LeaveRequest.class);
                                if (leaveRequest != null) {
                                    // Construct a string representation of the leave request
                                    String leaveDetails = "Employee ID: " + leaveRequest.getEmployeeId() + "\n" +
                                            "Start Date: " + leaveRequest.getStartDate() + "\n" +
                                            "End Date: " + leaveRequest.getEndDate() + "\n" +
                                            "Status: " + leaveRequest.getStatus() + "\n" +
                                            "Description: " + leaveRequest.getDescription();

                                    // Add leave details to the list
                                    leaveList.add(leaveDetails);
                                }
                            }
                        }

                        // Convert the list to an array for dialog display
                        String[] leaveArray = leaveList.toArray(new String[0]);

                        // Show dialog with leave records
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminOperationActivity.this);
                        builder.setTitle("All Leave Requests");
                        if (leaveArray.length > 0) {
                            builder.setItems(leaveArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Handle leave record selection if needed
                                }
                            });
                        } else {
                            builder.setMessage("No leave requests found");
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
                        Toast.makeText(AdminOperationActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        buttonEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminOperationActivity.this, EmployeeManagementActivity.class));
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Out");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performLogout();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void performLogout() {
        mAuth.signOut();
        startActivity(new Intent(AdminOperationActivity.this, MainActivity.class));
        finish();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(view);

        EditText editTextEmail = view.findViewById(R.id.editTextEmail);

        builder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = editTextEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    // Implement logic to send password reset link to email
                    sendPasswordResetLink(email);
                } else {
                    Toast.makeText(AdminOperationActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void sendPasswordResetLink(String email) {
        // Implement logic to send password reset link to the provided email
        // You can use FirebaseAuth to send password reset link
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminOperationActivity.this, "Password reset link sent to email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminOperationActivity.this, "Failed to send reset link. Please check the email address", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
