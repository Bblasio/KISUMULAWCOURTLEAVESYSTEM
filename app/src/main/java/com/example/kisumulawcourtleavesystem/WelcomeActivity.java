package com.example.kisumulawcourtleavesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button adminLoginButton, employeeLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        adminLoginButton = findViewById(R.id.adminloginbutton);
        employeeLoginButton = findViewById(R.id.employeeloginbutton);

        adminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Admin Login Activity
                Intent adminIntent = new Intent(WelcomeActivity.this, AdminActivity.class);
                startActivity(adminIntent);
            }
        });

        employeeLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Employee Login Activity
                Intent employeeIntent = new Intent(WelcomeActivity.this, EmployeeLoginActivity.class);
                startActivity(employeeIntent);
            }
        });
    }
}
