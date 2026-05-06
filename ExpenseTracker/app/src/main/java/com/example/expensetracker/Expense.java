package com.example.expensetracker;

public class Expense {

    int id;
    double amount;
    String category;
    String date;

    public Expense(int id, double amount, String category, String date) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
}