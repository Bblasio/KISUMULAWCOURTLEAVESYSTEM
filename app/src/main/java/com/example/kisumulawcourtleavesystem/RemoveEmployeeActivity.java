package com.example.kisumulawcourtleavesystem;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RemoveEmployeeActivity extends AppCompatActivity {

    private ListView listViewEmployees;
    private Button removeButton;
    private DatabaseReference mDatabase;
    private List<Employee> employeesList;
    private List<String> selectedEmployeesIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_employee);

        listViewEmployees = findViewById(R.id.listViewEmployees);
        removeButton = findViewById(R.id.removeButton);

        // Initialize Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("employees");

        employeesList = new ArrayList<>();
        selectedEmployeesIds = new ArrayList<>();

        // Retrieve employees from Firebase
        retrieveEmployees();

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedEmployees();
            }
        });
    }

    private void retrieveEmployees() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employeesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Employee employee = snapshot.getValue(Employee.class);
                    if (employee != null) {
                        employee.setId(snapshot.getKey()); // Set employee ID from Firebase key
                        employeesList.add(employee);
                    }
                }
                // Display employees in ListView
                ArrayAdapter<Employee> adapter = new ArrayAdapter<>(RemoveEmployeeActivity.this,
                        android.R.layout.simple_list_item_multiple_choice, employeesList);
                listViewEmployees.setAdapter(adapter);
                listViewEmployees.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RemoveEmployeeActivity.this, "Failed to retrieve employees", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeSelectedEmployees() {
        // Check if any employees are selected
        if (listViewEmployees.getCheckedItemCount() == 0) {
            Toast.makeText(this, "No employees selected for removal", Toast.LENGTH_SHORT).show();
            return;
        }

        // Iterate over the list view to find selected employees
        for (int i = 0; i < listViewEmployees.getCount(); i++) {
            if (listViewEmployees.isItemChecked(i) && i < employeesList.size()) {
                selectedEmployeesIds.add(employeesList.get(i).getId());
            }
        }

        // Remove selected employees from the database
        for (String employeeId : selectedEmployeesIds) {
            mDatabase.child(employeeId).removeValue();
        }

        // Clear the list of selected employees
        selectedEmployeesIds.clear();

        Toast.makeText(this, "Selected employees removed", Toast.LENGTH_SHORT).show();
    }
}
