package com.example.kisumulawcourtleavesystem;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementActivity extends AppCompatActivity implements EmployeeAdapter.OnEmployeeClickListener {

    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private List<Employee> employeeList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerViewEmployees);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fabAddEmployee = findViewById(R.id.fabAddEmployee);
        fabAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddEmployeeDialog();
            }
        });

        employeeList = new ArrayList<>();
        adapter = new EmployeeAdapter(employeeList, this);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            // User not authenticated, handle accordingly
            // For example, redirect to login screen
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("employees");
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                    Employee employee = dataSnapshot.getValue(Employee.class);
                    employeeList.add(employee);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                    // Handle changes to employee data
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    // Handle removed employee
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                    // Handle moved employee
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EmployeeManagementActivity.this, "Failed to load employees.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showAddEmployeeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_employee, null);
        dialogBuilder.setView(dialogView);

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextIdNumber = dialogView.findViewById(R.id.editTextIdNumber);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextGender = dialogView.findViewById(R.id.editTextGender);
        EditText editTextDesignation = dialogView.findViewById(R.id.editTextDesignation);
        EditText editTextDepartment = dialogView.findViewById(R.id.editTextDepartment);

        dialogBuilder.setTitle("Add Employee");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = editTextName.getText().toString().trim();
                String idNumber = editTextIdNumber.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String gender = editTextGender.getText().toString().trim();
                String designation = editTextDesignation.getText().toString().trim();
                String department = editTextDepartment.getText().toString().trim();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(idNumber) && !TextUtils.isEmpty(email) &&
                        !TextUtils.isEmpty(gender) && !TextUtils.isEmpty(designation) && !TextUtils.isEmpty(department)) {
                    addEmployee(name, idNumber, email, gender, designation, department);
                } else {
                    Toast.makeText(EmployeeManagementActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void addEmployee(String name, String idNumber, String email, String gender, String designation, String department) {
        String id = databaseReference.push().getKey();
        Employee employee = new Employee(id, name, idNumber, email, gender, designation, department);
        databaseReference.child(id).setValue(employee)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EmployeeManagementActivity.this, "Employee added successfully", Toast.LENGTH_SHORT).show();
                    // After adding employee to the database, create a corresponding user in Firebase Authentication
                    mAuth.createUserWithEmailAndPassword(email, idNumber) // Using email as username and ID number as password
                            .addOnSuccessListener(authResult -> {
                                // Employee user created successfully
                                // You can handle additional actions if needed
                            })
                            .addOnFailureListener(e -> {
                                // Handle any errors that occur while creating the user
                                Toast.makeText(EmployeeManagementActivity.this, "Failed to create employee user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(EmployeeManagementActivity.this, "Failed to add employee", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onEmployeeClick(Employee employee) {
        // Show dialog to edit/delete employee
        showEditDeleteEmployeeDialog(employee);
    }

    private void showEditDeleteEmployeeDialog(Employee employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit/Delete Employee");

        // Set up the buttons
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEditEmployeeDialog(employee);
            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteEmployee(employee);
            }
        });
        builder.setNeutralButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditEmployeeDialog(Employee employee) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_employee, null);
        dialogBuilder.setView(dialogView);

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextIdNumber = dialogView.findViewById(R.id.editTextIdNumber);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextGender = dialogView.findViewById(R.id.editTextGender);
        EditText editTextDesignation = dialogView.findViewById(R.id.editTextDesignation);
        EditText editTextDepartment = dialogView.findViewById(R.id.editTextDepartment);

        editTextName.setText(employee.getName());
        editTextIdNumber.setText(employee.getIdNumber());
        editTextEmail.setText(employee.getEmail());
        editTextGender.setText(employee.getGender());
        editTextDesignation.setText(employee.getDesignation());
        editTextDepartment.setText(employee.getDepartment());

        dialogBuilder.setTitle("Edit Employee");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = editTextName.getText().toString().trim();
                String idNumber = editTextIdNumber.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String gender = editTextGender.getText().toString().trim();
                String designation = editTextDesignation.getText().toString().trim();
                String department = editTextDepartment.getText().toString().trim();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(idNumber) && !TextUtils.isEmpty(email) &&
                        !TextUtils.isEmpty(gender) && !TextUtils.isEmpty(designation) && !TextUtils.isEmpty(department)) {
                    updateEmployee(employee, name, idNumber, email, gender, designation, department);
                } else {
                    Toast.makeText(EmployeeManagementActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void updateEmployee(Employee employee, String name, String idNumber, String email, String gender, String designation, String department) {
        DatabaseReference employeeRef = databaseReference.child(employee.getId());

        // Create a new Employee object with updated details
        Employee updatedEmployee = new Employee(employee.getId(), name, idNumber, email, gender, designation, department);

        employeeRef.setValue(updatedEmployee)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EmployeeManagementActivity.this, "Employee updated successfully", Toast.LENGTH_SHORT).show();
                        // Update the local list with the updated employee details
                        int index = employeeList.indexOf(employee);
                        if (index != -1) {
                            employeeList.set(index, updatedEmployee);
                            adapter.notifyItemChanged(index);
                        } else {
                            Toast.makeText(EmployeeManagementActivity.this, "Failed to update employee in local list", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EmployeeManagementActivity.this, "Failed to update employee", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteEmployee(Employee employee) {
        DatabaseReference employeeRef = databaseReference.child(employee.getId());
        employeeRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EmployeeManagementActivity.this, "Employee deleted successfully", Toast.LENGTH_SHORT).show();
                        // Remove employee from local list and notify adapter
                        employeeList.remove(employee);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EmployeeManagementActivity.this, "Failed to delete employee", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

