package com.example.expensetracker.model;

public class CategorySummary {
    private String category;
    private double total;

    public CategorySummary(String category, double total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() { return category; }
    public double getTotal() { return total; }
}
