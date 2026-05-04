package com.example.expensetracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.model.Expense;

import java.util.Locale;

public class ExpenseAdapter extends ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder> {

    public interface OnItemClickListener {
        void onEditClick(Expense expense);
        void onDeleteClick(Expense expense);
    }

    private OnItemClickListener listener;

    public ExpenseAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Expense> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Expense>() {
                @Override
                public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                    return oldItem.getId() == newItem.getId();
                }
                @Override
                public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle())
                            && oldItem.getAmount() == newItem.getAmount()
                            && oldItem.getCategory().equals(newItem.getCategory());
                }
            };

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = getItem(position);
        holder.bind(expense);
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle, tvAmount, tvCategory, tvDate, tvNote;
        private final ImageButton btnEdit, btnDelete;
        private final View categoryDot;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_expense_title);
            tvAmount = itemView.findViewById(R.id.tv_expense_amount);
            tvCategory = itemView.findViewById(R.id.tv_expense_category);
            tvDate = itemView.findViewById(R.id.tv_expense_date);
            tvNote = itemView.findViewById(R.id.tv_expense_note);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            categoryDot = itemView.findViewById(R.id.view_category_dot);
        }

        void bind(Expense expense) {
            tvTitle.setText(expense.getTitle());
            tvAmount.setText(String.format(Locale.getDefault(), "₹%.2f", expense.getAmount()));
            tvCategory.setText(expense.getCategory());
            tvDate.setText(expense.getDate());

            if (expense.getNote() != null && !expense.getNote().isEmpty()) {
                tvNote.setVisibility(View.VISIBLE);
                tvNote.setText(expense.getNote());
            } else {
                tvNote.setVisibility(View.GONE);
            }

            // Set category dot color
            categoryDot.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(getCategoryColor(expense.getCategory(), itemView.getContext()))
            );

            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(expense);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(expense);
            });
        }

        private int getCategoryColor(String category, Context context) {
            switch (category) {
                case "Food": return context.getColor(R.color.cat_food);
                case "Transport": return context.getColor(R.color.cat_transport);
                case "Shopping": return context.getColor(R.color.cat_shopping);
                case "Entertainment": return context.getColor(R.color.cat_entertainment);
                case "Health": return context.getColor(R.color.cat_health);
                case "Bills": return context.getColor(R.color.cat_bills);
                case "Education": return context.getColor(R.color.cat_education);
                default: return context.getColor(R.color.cat_other);
            }
        }
    }
}
