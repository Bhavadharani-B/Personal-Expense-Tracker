package com.example.expensetracker.ui.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ActivityAddEditExpenseBinding;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.viewmodel.ExpenseViewModel;

import java.util.Calendar;
import java.util.Locale;

public class AddEditExpenseActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_AMOUNT = "extra_amount";
    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_NOTE = "extra_note";

    private ActivityAddEditExpenseBinding binding;
    private ExpenseViewModel viewModel;
    private int expenseId = -1;

    private static final String[] CATEGORIES = {
        "Food", "Transport", "Shopping", "Entertainment", "Health", "Bills", "Education", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        binding.spinnerCategory.setAdapter(categoryAdapter);

        // Date picker
        binding.etDate.setFocusable(false);
        binding.etDate.setOnClickListener(v -> showDatePicker());

        // Set today's date by default
        Calendar cal = Calendar.getInstance();
        String today = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        binding.etDate.setText(today);

        // Check if editing existing expense
        if (getIntent().hasExtra(EXTRA_EXPENSE_ID)) {
            expenseId = getIntent().getIntExtra(EXTRA_EXPENSE_ID, -1);
            getSupportActionBar().setTitle("Edit Expense");
            binding.etTitle.setText(getIntent().getStringExtra(EXTRA_TITLE));
            binding.etAmount.setText(String.valueOf(getIntent().getDoubleExtra(EXTRA_AMOUNT, 0)));
            binding.etDate.setText(getIntent().getStringExtra(EXTRA_DATE));
            binding.etNote.setText(getIntent().getStringExtra(EXTRA_NOTE));

            String cat = getIntent().getStringExtra(EXTRA_CATEGORY);
            for (int i = 0; i < CATEGORIES.length; i++) {
                if (CATEGORIES[i].equals(cat)) {
                    binding.spinnerCategory.setSelection(i);
                    break;
                }
            }
        } else {
            getSupportActionBar().setTitle("Add Expense");
        }

        binding.btnSave.setOnClickListener(v -> saveExpense());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            binding.etDate.setText(date);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveExpense() {
        String title = binding.etTitle.getText().toString().trim();
        String amountStr = binding.etAmount.getText().toString().trim();
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String date = binding.etDate.getText().toString().trim();
        String note = binding.etNote.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            binding.etTitle.setError("Title is required");
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            binding.etAmount.setError("Amount is required");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            binding.etAmount.setError("Invalid amount");
            return;
        }

        if (expenseId == -1) {
            Expense expense = new Expense(title, amount, category, date, note);
            viewModel.insert(expense);
            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show();
        } else {
            Expense expense = new Expense(title, amount, category, date, note);
            expense.setId(expenseId);
            viewModel.update(expense);
            Toast.makeText(this, "Expense updated!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
