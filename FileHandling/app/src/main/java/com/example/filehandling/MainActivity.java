package com.example.filehandling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button btnSave, btnRead;
    TextView textView;

    String fileName = "myfile.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        btnSave = findViewById(R.id.btnSave);
        btnRead = findViewById(R.id.btnRead);
        textView = findViewById(R.id.textView);

        // SAVE FILE
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data = editText.getText().toString();

                try {
                    FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);

                    fos.write(data.getBytes());

                    fos.close();

                    Toast.makeText(MainActivity.this,
                            "File Saved",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // READ FILE
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    FileInputStream fis = openFileInput(fileName);

                    int c;
                    String temp = "";

                    while ((c = fis.read()) != -1) {
                        temp = temp + Character.toString((char) c);
                    }

                    textView.setText(temp);

                    fis.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}