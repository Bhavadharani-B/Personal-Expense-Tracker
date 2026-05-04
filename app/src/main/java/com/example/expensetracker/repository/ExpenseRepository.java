package com.example.expensetracker.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.expensetracker.dao.ExpenseDao;
import com.example.expensetracker.database.ExpenseDatabase;
import com.example.expensetracker.model.Expense;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {

    private final ExpenseDao expenseDao;
    private final ExecutorService executorService;

    public ExpenseRepository(Application application) {
        ExpenseDatabase db = ExpenseDatabase.getInstance(application);
        expenseDao = db.expenseDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Expense expense) {
        executorService.execute(() -> expenseDao.insert(expense));
    }

    public void update(Expense expense) {
        executorService.execute(() -> expenseDao.update(expense));
    }

    public void delete(Expense expense) {
        executorService.execute(() -> expenseDao.delete(expense));
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return expenseDao.getAllExpenses();
    }

    public LiveData<List<Expense>> getExpensesByMonth(String yearMonth) {
        return expenseDao.getExpensesByMonth(yearMonth);
    }

    public LiveData<Double> getTotalByMonth(String yearMonth) {
        return expenseDao.getTotalByMonth(yearMonth);
    }

    public LiveData<List<ExpenseDao.CategoryRow>> getCategoryTotalsByMonth(String yearMonth) {
        return expenseDao.getCategoryTotalsByMonth(yearMonth);
    }

    public LiveData<List<ExpenseDao.CategoryRow>> getAllCategoryTotals() {
        return expenseDao.getAllCategoryTotals();
    }
}
