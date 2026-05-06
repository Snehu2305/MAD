package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseDB";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount REAL," +
                "category TEXT," +
                "date TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expenses");
        onCreate(db);
    }

    // INSERT
    public void insertExpense(double amount, String category, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("category", category);
        values.put("date", date);

        db.insert("expenses", null, values);
    }

    // TOTAL
    public double getTotalExpense() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM expenses", null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    // ALL EXPENSES
    public ArrayList<Expense> getAllExpenses() {
        ArrayList<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM expenses ORDER BY date DESC", null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new Expense(
                        cursor.getInt(0),
                        cursor.getDouble(1),
                        cursor.getString(2),
                        cursor.getString(3)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    // DELETE
    public void deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM expenses WHERE id=" + id);
    }

    // CATEGORY TOTAL (ALL)
    public HashMap<String, Float> getCategoryTotals() {
        HashMap<String, Float> map = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT category, SUM(amount) FROM expenses GROUP BY category",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                map.put(cursor.getString(0), cursor.getFloat(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return map;
    }

    // FILTERED EXPENSES (FIXED WITH LOCALTIME)
    public ArrayList<Expense> getExpensesByMonth(String filter) {

        ArrayList<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query;

        filter = filter.trim();
        if (filter.equalsIgnoreCase("This Month")) {
            query = "SELECT * FROM expenses WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now', 'localtime')";
        }
        else if (filter.equalsIgnoreCase("Last Month")) {
            query = "SELECT * FROM expenses WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now', 'localtime', '-1 month')";
        }
        else {
            query = "SELECT * FROM expenses";
        }

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new Expense(
                        cursor.getInt(0),
                        cursor.getDouble(1),
                        cursor.getString(2),
                        cursor.getString(3)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    // FILTERED CATEGORY TOTALS (FIXED WITH LOCALTIME)
    public HashMap<String, Float> getCategoryTotalsByMonth(String filter) {

        HashMap<String, Float> map = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query;
        filter = filter.trim();

        if (filter.equalsIgnoreCase("This Month")) {
            query = "SELECT category, SUM(amount) FROM expenses " +
                    "WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now', 'localtime') " +
                    "GROUP BY category";
        }
        else if (filter.equalsIgnoreCase("Last Month")) {
            query = "SELECT category, SUM(amount) FROM expenses " +
                    "WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now', 'localtime', '-1 month') " +
                    "GROUP BY category";
        }
        else {
            query = "SELECT category, SUM(amount) FROM expenses GROUP BY category";
        }

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                map.put(cursor.getString(0), cursor.getFloat(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return map;
    }

    public double getTotalExpenseByMonth(String filter) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query;

        filter = filter.trim();

        if (filter.equalsIgnoreCase("This Month")) {
            query = "SELECT SUM(amount) FROM expenses WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now', 'localtime')";
        }
        else if (filter.equalsIgnoreCase("Last Month")) {
            query = "SELECT SUM(amount) FROM expenses WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now', 'localtime', '-1 month')";
        }
        else {
            query = "SELECT SUM(amount) FROM expenses";
        }

        Cursor cursor = db.rawQuery(query, null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        return total;
    }
}