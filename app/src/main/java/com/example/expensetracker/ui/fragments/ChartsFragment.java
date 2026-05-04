package com.example.expensetracker.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.dao.ExpenseDao;
import com.example.expensetracker.databinding.FragmentChartsBinding;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ChartsFragment extends Fragment {

    private FragmentChartsBinding binding;
    private ExpenseViewModel viewModel;

    private static final int[] CHART_COLORS = {
        0xFFE74C3C, 0xFF3498DB, 0xFF2ECC71, 0xFFF39C12,
        0xFF9B59B6, 0xFF1ABC9C, 0xFFE67E22, 0xFF95A5A6
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChartsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        setupPieChart();
        setupBarChart();

        viewModel.allCategoryTotals.observe(getViewLifecycleOwner(), this::updatePieChart);
        viewModel.categoryTotalsByMonth.observe(getViewLifecycleOwner(), this::updateBarChart);
    }

    private void setupPieChart() {
        PieChart chart = binding.pieChart;
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(40f);
        chart.setTransparentCircleRadius(45f);
        chart.setDrawCenterText(true);
        chart.setCenterText("Spending\nby Category");
        chart.setCenterTextSize(14f);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
    }

    private void updatePieChart(List<ExpenseDao.CategoryRow> rows) {
        if (rows == null || rows.isEmpty()) {
            binding.pieChart.setVisibility(View.GONE);
            binding.tvNoDataPie.setVisibility(View.VISIBLE);
            return;
        }
        binding.pieChart.setVisibility(View.VISIBLE);
        binding.tvNoDataPie.setVisibility(View.GONE);

        List<PieEntry> entries = new ArrayList<>();
        for (ExpenseDao.CategoryRow row : rows) {
            entries.add(new PieEntry((float) row.total, row.category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(getColors(entries.size()));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);

        binding.pieChart.setData(data);
        binding.pieChart.invalidate();
        binding.pieChart.animateY(800);
    }

    private void setupBarChart() {
        BarChart chart = binding.barChart;
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setFitBars(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-30f);
        xAxis.setTextSize(10f);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextSize(10f);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);
    }

    private void updateBarChart(List<ExpenseDao.CategoryRow> rows) {
        if (rows == null || rows.isEmpty()) {
            binding.barChart.setVisibility(View.GONE);
            binding.tvNoDataBar.setVisibility(View.VISIBLE);
            return;
        }
        binding.barChart.setVisibility(View.VISIBLE);
        binding.tvNoDataBar.setVisibility(View.GONE);

        List<BarEntry> entries = new ArrayList<>();
        String[] labels = new String[rows.size()];

        for (int i = 0; i < rows.size(); i++) {
            entries.add(new BarEntry(i, (float) rows.get(i).total));
            labels[i] = rows.get(i).category;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Expenses");
        dataSet.setColors(getColors(entries.size()));
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        binding.barChart.setData(data);
        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.barChart.getXAxis().setLabelCount(labels.length);
        binding.barChart.invalidate();
        binding.barChart.animateY(800);
    }

    private List<Integer> getColors(int count) {
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            colors.add(CHART_COLORS[i % CHART_COLORS.length]);
        }
        return colors;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
