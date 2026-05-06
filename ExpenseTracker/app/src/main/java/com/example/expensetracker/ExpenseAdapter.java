package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    ArrayList<Expense> list;
    DBHelper dbHelper;
    OnExpenseDeleted listener;

    public ExpenseAdapter(ArrayList<Expense> list, DBHelper dbHelper, OnExpenseDeleted listener) {
        this.list = list;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category, amount, date;

        public ViewHolder(View view) {
            super(view);
            category = view.findViewById(R.id.categoryText);
            amount = view.findViewById(R.id.amountText);
            date = view.findViewById(R.id.dateText); // ✅ correct place
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Expense e = list.get(position);

        holder.category.setText(e.getCategory());
        holder.amount.setText("₹ " + e.getAmount());
        holder.date.setText(e.getDate());

        // Long press to delete
        holder.itemView.setOnLongClickListener(v -> {
            new android.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Delete")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.deleteExpense(e.getId());
                        list.remove(position);
                        notifyDataSetChanged();
                        listener.onDeleted();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}