package com.example.databaseapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    Button b1, b2, b3;
    TextView t1;
    EditText eid, ename, ediv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Make sure your root layout in XML has android:id="@+id/main"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);

        t1 = findViewById(R.id.textView3);

        eid = findViewById(R.id.editText1);
        ename = findViewById(R.id.editText2);
        ediv = findViewById(R.id.editText3);

        // Create/Open Database
        try {
            db = openOrCreateDatabase("StudentDB", MODE_PRIVATE, null);

            db.execSQL("CREATE TABLE IF NOT EXISTS Temp (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "div TEXT)");
        }
        catch (SQLException e) {
            Toast.makeText(this,
                    "Database Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        // INSERT BUTTON
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rollNo = eid.getText().toString().trim();
                String name = ename.getText().toString().trim();
                String div = ediv.getText().toString().trim();

                // Validation
                if (rollNo.isEmpty() || name.isEmpty() || div.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int id;

                // Check numeric ID
                try {
                    id = Integer.parseInt(rollNo);
                }
                catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this,
                            "ID must be numeric",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put("id", id);
                values.put("name", name);
                values.put("div", div);

                long result = db.insert("Temp", null, values);

                if (result != -1) {
                    Toast.makeText(MainActivity.this,
                            "Record Inserted Successfully",
                            Toast.LENGTH_SHORT).show();

                    eid.setText("");
                    ename.setText("");
                    ediv.setText("");
                }
                else {
                    Toast.makeText(MainActivity.this,
                            "Insert Failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // DISPLAY BUTTON
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor c = db.rawQuery("SELECT * FROM Temp", null);

                if (c.getCount() == 0) {
                    t1.setText("No Records Found");
                    c.close();
                    return;
                }

                StringBuilder sb = new StringBuilder();

                sb.append("ID\t\tName\t\tDiv\n");
                sb.append("-------------------------\n");

                while (c.moveToNext()) {

                    sb.append(c.getInt(0)).append("\t\t");
                    sb.append(c.getString(1)).append("\t\t");
                    sb.append(c.getString(2)).append("\n");
                }

                t1.setText(sb.toString());

                c.close();
            }
        });

        // DELETE ALL BUTTON
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int deletedRows = db.delete("Temp", null, null);

                if (deletedRows > 0) {

                    Toast.makeText(MainActivity.this,
                            "All Records Deleted",
                            Toast.LENGTH_SHORT).show();

                    t1.setText("");
                }
                else {

                    Toast.makeText(MainActivity.this,
                            "No Records Found",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}