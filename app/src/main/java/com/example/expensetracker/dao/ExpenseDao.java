package com.example.expensetracker.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expensetracker.model.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    void insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE strftime('%Y-%m', date) = :yearMonth ORDER BY date DESC")
    LiveData<List<Expense>> getExpensesByMonth(String yearMonth);

    @Query("SELECT SUM(amount) FROM expenses WHERE strftime('%Y-%m', date) = :yearMonth")
    LiveData<Double> getTotalByMonth(String yearMonth);

    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE strftime('%Y-%m', date) = :yearMonth GROUP BY category")
    LiveData<List<CategoryRow>> getCategoryTotalsByMonth(String yearMonth);

    @Query("SELECT category, SUM(amount) as total FROM expenses GROUP BY category")
    LiveData<List<CategoryRow>> getAllCategoryTotals();

    // Inner class for category aggregation
    class CategoryRow {
        public String category;
        public double total;
    }
}
