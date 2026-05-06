package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    EditText amountInput, categoryInput, dateInput;
    Button saveBtn;
    DBHelper dbHelper;

    Calendar calendar;
    String selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        amountInput = findViewById(R.id.amountInput);
        categoryInput = findViewById(R.id.categoryInput);
        dateInput = findViewById(R.id.dateInput);
        saveBtn = findViewById(R.id.saveBtn);

        dbHelper = new DBHelper(this);
        calendar = Calendar.getInstance();

        // Set default current date
        updateDateField();

        // Open calendar on click
        dateInput.setOnClickListener(v -> {
            new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        updateDateField();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Save button
        saveBtn.setOnClickListener(v -> {

            String amountStr = amountInput.getText().toString();
            String category = categoryInput.getText().toString();

            if (amountStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            // Save selected date
            dbHelper.insertExpense(amount, category, selectedDateTime);

            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateDateField() {

        // Format for DB (IMPORTANT)
        SimpleDateFormat dbFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        selectedDateTime = dbFormat.format(calendar.getTime());

        // Format for UI
        SimpleDateFormat displayFormat =
                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        dateInput.setText(displayFormat.format(calendar.getTime()));
    }
}