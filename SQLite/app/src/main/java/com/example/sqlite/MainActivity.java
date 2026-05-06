package com.example.sqlite;

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

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;

    EditText eid, ename, ediv;
    Button b1, b2, b3;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        eid = findViewById(R.id.editText1);
        ename = findViewById(R.id.editText2);
        ediv = findViewById(R.id.editText3);

        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);

        t1 = findViewById(R.id.textView3);

        // Create/Open Database
        try {
            db = openOrCreateDatabase("StudentDB", MODE_PRIVATE, null);

            db.execSQL("CREATE TABLE IF NOT EXISTS Temp(" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "div TEXT)");
        }
        catch (SQLException e) {

            Toast.makeText(this,
                    "Database Error",
                    Toast.LENGTH_LONG).show();
        }

        // INSERT BUTTON
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String roll = eid.getText().toString().trim();
                String name = ename.getText().toString().trim();
                String div = ediv.getText().toString().trim();

                if (roll.isEmpty() || name.isEmpty() || div.isEmpty()) {

                    Toast.makeText(MainActivity.this,
                            "Fill all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int id;

                try {
                    id = Integer.parseInt(roll);
                }
                catch (NumberFormatException e) {

                    Toast.makeText(MainActivity.this,
                            "ID must be number",
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
                            "Record Inserted",
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

                while (c.moveToNext()) {

                    sb.append("ID : ").append(c.getInt(0)).append("\n");
                    sb.append("Name : ").append(c.getString(1)).append("\n");
                    sb.append("Div : ").append(c.getString(2)).append("\n\n");
                }

                t1.setText(sb.toString());

                c.close();
            }
        });

        // DELETE BUTTON
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