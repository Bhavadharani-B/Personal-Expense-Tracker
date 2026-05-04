package com.example.expensetracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.expensetracker.dao.ExpenseDao;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {

    private final ExpenseRepository repository;
    private final MutableLiveData<String> currentYearMonth = new MutableLiveData<>();

    public final LiveData<List<Expense>> allExpenses;
    public final LiveData<List<Expense>> expensesByMonth;
    public final LiveData<Double> totalByMonth;
    public final LiveData<List<ExpenseDao.CategoryRow>> categoryTotalsByMonth;
    public final LiveData<List<ExpenseDao.CategoryRow>> allCategoryTotals;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);

        allExpenses = repository.getAllExpenses();
        allCategoryTotals = repository.getAllCategoryTotals();

        expensesByMonth = Transformations.switchMap(currentYearMonth,
                yearMonth -> repository.getExpensesByMonth(yearMonth));

        totalByMonth = Transformations.switchMap(currentYearMonth,
                yearMonth -> repository.getTotalByMonth(yearMonth));

        categoryTotalsByMonth = Transformations.switchMap(currentYearMonth,
                yearMonth -> repository.getCategoryTotalsByMonth(yearMonth));
    }

    public void setCurrentYearMonth(String yearMonth) {
        currentYearMonth.setValue(yearMonth);
    }

    public String getCurrentYearMonth() {
        return currentYearMonth.getValue();
    }

    public void insert(Expense expense) { repository.insert(expense); }
    public void update(Expense expense) { repository.update(expense); }
    public void delete(Expense expense) { repository.delete(expense); }
}
