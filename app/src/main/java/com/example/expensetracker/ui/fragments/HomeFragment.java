package com.example.expensetracker.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.R;
import com.example.expensetracker.adapter.ExpenseAdapter;
import com.example.expensetracker.databinding.FragmentHomeBinding;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.ui.activities.AddEditExpenseActivity;
import com.example.expensetracker.viewmodel.ExpenseViewModel;

import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ExpenseViewModel viewModel;
    private ExpenseAdapter adapter;

    private int currentYear;
    private int currentMonth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        // Set current month
        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH) + 1;
        updateMonthDisplay();

        // Setup RecyclerView
        adapter = new ExpenseAdapter();
        binding.recyclerExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerExpenses.setAdapter(adapter);

        adapter.setOnItemClickListener(new ExpenseAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Expense expense) {
                Intent intent = new Intent(getActivity(), AddEditExpenseActivity.class);
                intent.putExtra(AddEditExpenseActivity.EXTRA_EXPENSE_ID, expense.getId());
                intent.putExtra(AddEditExpenseActivity.EXTRA_TITLE, expense.getTitle());
                intent.putExtra(AddEditExpenseActivity.EXTRA_AMOUNT, expense.getAmount());
                intent.putExtra(AddEditExpenseActivity.EXTRA_CATEGORY, expense.getCategory());
                intent.putExtra(AddEditExpenseActivity.EXTRA_DATE, expense.getDate());
                intent.putExtra(AddEditExpenseActivity.EXTRA_NOTE, expense.getNote());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Expense expense) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Expense")
                        .setMessage("Are you sure you want to delete \"" + expense.getTitle() + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> viewModel.delete(expense))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // Observe data
        viewModel.expensesByMonth.observe(getViewLifecycleOwner(), expenses -> {
            adapter.submitList(expenses);
            binding.tvEmptyState.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerExpenses.setVisibility(expenses.isEmpty() ? View.GONE : View.VISIBLE);
        });

        viewModel.totalByMonth.observe(getViewLifecycleOwner(), total -> {
            double amount = total != null ? total : 0.0;
            binding.tvMonthTotal.setText(String.format(Locale.getDefault(), "₹%.2f", amount));
        });

        // Month navigation
        binding.btnPrevMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 1) { currentMonth = 12; currentYear--; }
            updateMonthDisplay();
        });

        binding.btnNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 12) { currentMonth = 1; currentYear++; }
            updateMonthDisplay();
        });

        // FAB
        binding.fabAddExpense.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddEditExpenseActivity.class)));
    }

    private void updateMonthDisplay() {
        String yearMonth = String.format(Locale.getDefault(), "%04d-%02d", currentYear, currentMonth);
        String[] months = {"January","February","March","April","May","June",
                           "July","August","September","October","November","December"};
        binding.tvCurrentMonth.setText(months[currentMonth - 1] + " " + currentYear);
        viewModel.setCurrentYearMonth(yearMonth);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
