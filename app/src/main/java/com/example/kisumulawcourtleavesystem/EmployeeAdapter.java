package com.example.kisumulawcourtleavesystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employeeList;
    private OnEmployeeClickListener listener;

    public EmployeeAdapter(List<Employee> employeeList, OnEmployeeClickListener listener) {
        this.employeeList = employeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_employee_item, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.bind(employee);
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewName;
        private TextView textViewIdNumber;
        private TextView textViewEmail;
        private TextView textViewGender;
        private TextView textViewDesignation;
        private TextView textViewDepartment;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewIdNumber = itemView.findViewById(R.id.textViewIdNumber);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewGender = itemView.findViewById(R.id.textViewGender);
            textViewDesignation = itemView.findViewById(R.id.textViewDesignation);
            textViewDepartment = itemView.findViewById(R.id.textViewDepartment);

            // Set click listener
            itemView.setOnClickListener(this);
        }

        public void bind(Employee employee) {
            textViewName.setText(employee.getName());
            textViewIdNumber.setText(employee.getIdNumber());
            textViewEmail.setText(employee.getEmail());
            textViewGender.setText(employee.getGender());
            textViewDesignation.setText(employee.getDesignation());
            textViewDepartment.setText(employee.getDepartment());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                // Perform login operation when an employee item is clicked
                Employee clickedEmployee = employeeList.get(position);
                listener.onEmployeeClick(clickedEmployee);
            }
        }
    }

    public interface OnEmployeeClickListener {
        void onEmployeeClick(Employee employee);
    }

    public void updateList(List<Employee> newList) {
        employeeList.clear();
        employeeList.addAll(newList);
        notifyDataSetChanged();
    }
}
