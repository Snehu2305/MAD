package com.example.inputcontrol;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etName;
    RadioGroup radioGroup;
    CheckBox cbMusic, cbSports, cbDrawing, cbDance;
    Spinner spCity;
    ToggleButton toggleSubscribe;
    Switch switchNotify;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        radioGroup = findViewById(R.id.radioGroup);

        cbMusic = findViewById(R.id.cbMusic);
        cbSports = findViewById(R.id.cbSports);
        cbDrawing = findViewById(R.id.cbDrawing);
        cbDance = findViewById(R.id.cbDance);

        spCity = findViewById(R.id.spCity);

        toggleSubscribe = findViewById(R.id.toggleSubscribe);
        switchNotify = findViewById(R.id.switchNotify);

        btnSubmit = findViewById(R.id.btnSubmit);

        // Spinner Data
        String[] cities = {"Mumbai", "Pune", "Delhi", "Bangalore"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                cities
        );

        spCity.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> {

            String name = etName.getText().toString();

            // Gender
            int selectedId = radioGroup.getCheckedRadioButtonId();

            String gender = "Not Selected";

            if (selectedId != -1) {
                RadioButton rb = findViewById(selectedId);
                gender = rb.getText().toString();
            }

            // Hobbies
            StringBuilder hobbies = new StringBuilder();

            if (cbMusic.isChecked())
                hobbies.append("Music ");

            if (cbSports.isChecked())
                hobbies.append("Sports ");

            if (cbDrawing.isChecked())
                hobbies.append("Drawing ");

            if (cbDance.isChecked())
                hobbies.append("Dance ");

            // Spinner
            String city = spCity.getSelectedItem().toString();

            // Toggle
            String subscription;

            if (toggleSubscribe.isChecked()) {
                subscription = "Subscribed";
            } else {
                subscription = "Not Subscribed";
            }

            // Switch
            String notification;

            if (switchNotify.isChecked()) {
                notification = "Enabled";
            } else {
                notification = "Disabled";
            }

            // Output
            String result =
                    "Name: " + name +
                            "\nGender: " + gender +
                            "\nHobbies: " + hobbies +
                            "\nCity: " + city +
                            "\nSubscription: " + subscription +
                            "\nNotification: " + notification;

            Toast.makeText(MainActivity.this,
                    result,
                    Toast.LENGTH_LONG).show();
        });
    }
}