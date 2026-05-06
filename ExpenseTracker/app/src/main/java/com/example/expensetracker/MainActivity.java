package com.example.expensetracker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.widget.ImageButton;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity implements OnExpenseDeleted {

    FloatingActionButton addExpenseBtn;
    DBHelper dbHelper;
    TextView totalExpense;
    RecyclerView recyclerView;
    ExpenseAdapter adapter;
    PieChart pieChart;
    Switch themeSwitch;

    // 🔥 NEW: Spinner filter
    Spinner filterSpinner;
    String selectedFilter = "All";

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize prefs FIRST
        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        // Apply saved theme BEFORE UI loads
        int savedMode = prefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(savedMode);

        setContentView(R.layout.activity_main);

        addExpenseBtn = findViewById(R.id.addExpenseBtn);
        totalExpense = findViewById(R.id.totalExpense);
        recyclerView = findViewById(R.id.expenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pieChart = findViewById(R.id.pieChart);
        themeSwitch = findViewById(R.id.themeSwitch);

        // 🔥 NEW: Spinner init
        filterSpinner = findViewById(R.id.filterSpinner);

        dbHelper = new DBHelper(this);

        // Open Add Expense screen
        addExpenseBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        // Theme toggle
        // Set initial state
        int currentMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        themeSwitch.setChecked(currentMode == Configuration.UI_MODE_NIGHT_YES);

// Toggle listener
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            SharedPreferences.Editor editor = prefs.edit();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putInt("theme", AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putInt("theme", AppCompatDelegate.MODE_NIGHT_NO);
            }

            editor.apply();
        });

        // 🔥 NEW: Setup Spinner
        setupFilterSpinner();

    }

    // 🔥 NEW METHOD
    private void setupFilterSpinner() {

        ArrayList<String> filters = new ArrayList<>();
        filters.add("All");
        filters.add("This Month");
        filters.add("Last Month");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                filters
        );

        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedFilter = filters.get(position);
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {

        // 🔥 USE SELECTED FILTER
        String selected = selectedFilter;
        Log.d("FILTER", selected);

        // Total
        double total = dbHelper.getTotalExpenseByMonth(selected);
        totalExpense.setText("₹ " + total);

        // List
        ArrayList<Expense> list = dbHelper.getExpensesByMonth(selected);
        adapter = new ExpenseAdapter(list, dbHelper, this);
        recyclerView.setAdapter(adapter);

        // Chart
        HashMap<String, Float> data = dbHelper.getCategoryTotalsByMonth(selected);
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Float> entry : data.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        // Donut style
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(60f);
        pieChart.setTransparentCircleRadius(65f);

        // Center text
        pieChart.setCenterText("₹ " + total);
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.parseColor("#6200EE"));

        // Labels
        pieChart.setEntryLabelColor(Color.BLACK);

        // Legend color (dark mode support)
        int nightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        Legend legend = pieChart.getLegend();

        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            legend.setTextColor(Color.WHITE);
        } else {
            legend.setTextColor(Color.BLACK);
        }

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        pieChart.animateY(800);
        pieChart.invalidate();
    }

    @Override
    public void onDeleted() {
        updateUI();
    }
}