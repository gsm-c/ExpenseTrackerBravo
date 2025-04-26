package com.expensetracker.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.table.DefaultTableModel;

import javax.swing.table.DefaultTableCellRenderer;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.expensetracker.models.Transaction;
import com.expensetracker.reports.UserReport;
import javax.swing.*;
import java.awt.*;

public class UserReportViewerDialog extends JDialog {
    public UserReportViewerDialog(UserReport report) {
        setTitle("User Report - " + report.getUser().getUsername());
        setSize(800, 600);
        setModal(true);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Summary tab
        JTextArea summaryArea = new JTextArea(report.getReportContent());
        summaryArea.setEditable(false);
        tabbedPane.addTab("Summary", new JScrollPane(summaryArea));

        // Transactions tab
        JTable transactionsTable = createTransactionsTable(report);
        tabbedPane.addTab("Transactions", new JScrollPane(transactionsTable));

        // Charts tab
        JPanel chartPanel = createChartPanel(report);
        tabbedPane.addTab("Charts", chartPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTable createTransactionsTable(UserReport report) {
        String[] columns = {"Date", "Type", "Category", "Amount", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? Double.class : String.class;
            }
        };

        // Populate table
        for (Transaction t : report.getRecentTransactions()) {
            model.addRow(new Object[]{
                    t.getDate(),
                    t.getType(),
                    t.getCategory(),
                    t.getAmount(),
                    t.getDescription()
            });
        }

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);

        // Fix the renderer implementation
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                // Call parent implementation first
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                // Format currency
                if (value instanceof Number) {
                    setText(String.format("$%.2f", ((Number)value).doubleValue()));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return c;
            }
        });

        return table;
    }

    private JPanel createChartPanel(UserReport report) {
        JTabbedPane chartTabs = new JTabbedPane();

        // 1. Pie Chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        Map<String, Double> expensesByCategory = new HashMap<>();

        // Calculate expenses by category
        for (Transaction t : report.getRecentTransactions()) {
            if ("expense".equalsIgnoreCase(t.getType())) {
                String category = t.getCategory();
                double amount = t.getAmount();
                expensesByCategory.put(
                        category,
                        expensesByCategory.getOrDefault(category, 0.0) + amount
                );
            }
        }

        // Add to dataset (using explicit double values)
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Expense Categories", pieDataset, true, true, false);
        chartTabs.addTab("Expenses", new ChartPanel(pieChart));

        // 2. Monthly Trends Chart
        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        report.getMonthlySummaries().forEach((month, balance) -> {
            lineDataset.addValue(balance, "Balance", month);
        });

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Monthly Trends", "Month", "Amount ($)", lineDataset);
        chartTabs.addTab("Trends", new ChartPanel(lineChart));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartTabs, BorderLayout.CENTER);
        return panel;
    }

    private ChartPanel createExpensePieChart(UserReport report) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Get expense totals by category
        Map<String, Double> expensesByCategory = new HashMap<>();
        for (Transaction t : report.getRecentTransactions()) {
            if ("expense".equalsIgnoreCase(t.getType())) {
                expensesByCategory.merge(t.getCategory(), t.getAmount(), Double::sum);
            }
        }

        // Add to dataset
        expensesByCategory.forEach(dataset::setValue);

        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Expense Categories",
                dataset,
                true, true, false);

        return new ChartPanel(chart);
    }

    private ChartPanel createMonthlyTrendChart(UserReport report) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Add monthly data
        report.getMonthlySummaries().forEach((month, balance) -> {
            dataset.addValue(balance, "Balance", month);
        });

        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Monthly Balance Trend",
                "Month",
                "Amount ($)",
                dataset);

        return new ChartPanel(chart);
    }
}