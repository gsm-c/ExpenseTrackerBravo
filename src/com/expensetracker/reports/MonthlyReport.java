package com.expensetracker.reports;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MonthlyReport extends Report {
    private final double totalIncome;
    private final double totalExpenses;
    private final Map<String, Double> expensesByCategory;
    private final int month;
    private final int year;

    public MonthlyReport(double totalIncome, double totalExpenses,
                         Map<String, Double> expensesByCategory, int month, int year) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.expensesByCategory = expensesByCategory;
        this.month = month;
        this.year = year;
        this.generatedDate = LocalDate.now();
        this.title = String.format("Monthly Report - %s %d",
                DateTimeFormatter.ofPattern("MMMM").format(LocalDate.of(year, month, 1)),
                year);
    }

    @Override
    public String getReportContent() {
        StringBuilder content = new StringBuilder();
        content.append("=".repeat(50)).append("\n");
        content.append(title).append("\n");
        content.append("=".repeat(50)).append("\n\n");
        content.append(String.format("Generated on: %s%n%n",
                generatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        // summary
        content.append("SUMMARY\n");
        content.append("-".repeat(50)).append("\n");
        content.append(String.format("Total Income: $%.2f%n", totalIncome));
        content.append(String.format("Total Expenses: $%.2f%n", totalExpenses));
        content.append(String.format("Net Balance: $%.2f%n%n", totalIncome - totalExpenses));

        // expense breakdown
        if (expensesByCategory != null && !expensesByCategory.isEmpty()) {
            content.append("EXPENSE BREAKDOWN\n");
            content.append("-".repeat(50)).append("\n");
            expensesByCategory.forEach((category, amount) -> {
                double percentage = (amount / totalExpenses) * 100;
                content.append(String.format("- %-15s: $%-10.2f (%.1f%%)%n",
                        category, amount, percentage));
            });
        }
        content.append("\n").append("=".repeat(50)).append("\n");
        return content.toString();
    }

    // getters
    public double getTotalIncome() { return totalIncome; }
    public double getTotalExpenses() { return totalExpenses; }
    public Map<String, Double> getExpensesByCategory() { return expensesByCategory; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
}


