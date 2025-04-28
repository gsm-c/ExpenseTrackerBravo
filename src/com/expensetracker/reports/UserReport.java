package com.expensetracker.reports;

import com.expensetracker.models.Transaction;
import com.expensetracker.models.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class UserReport extends Report {
    private final User user;
    private final double totalBalance;
    private final Map<String, Double> monthlySummaries;
    private final List<Transaction> recentTransactions;

    public UserReport(User user, double totalBalance,
                      Map<String, Double> monthlySummaries,
                      List<Transaction> recentTransactions) {
        this.user = user;
        this.totalBalance = totalBalance;
        this.monthlySummaries = monthlySummaries;
        this.recentTransactions = recentTransactions;
        this.generatedDate = LocalDate.now();
        this.title = String.format("User Report - %s", user.getUsername());
    }

    @Override
    public String getReportContent() {
        StringBuilder content = new StringBuilder();
        content.append("=".repeat(50)).append("\n");
        content.append(title).append("\n");
        content.append("=".repeat(50)).append("\n\n");
        content.append(String.format("Generated on: %s%n%n",
                generatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        // user info
        content.append("USER INFORMATION\n");
        content.append("-".repeat(50)).append("\n");
        content.append(String.format("Username: %s%n", user.getUsername()));
        content.append(String.format("Role: %s%n", user.getRole()));
        content.append(String.format("Current Balance: $%.2f%n%n", totalBalance));

        // monthly summaries
        if (monthlySummaries != null && !monthlySummaries.isEmpty()) {
            content.append("MONTHLY SUMMARIES (Last 6 Months)\n");
            content.append("-".repeat(50)).append("\n");
            monthlySummaries.forEach((monthYear, balance) -> {
                content.append(String.format("- %-15s: $%.2f%n", monthYear, balance));
            });
            content.append("\n");
        }

        // recent transactions
        if (recentTransactions != null && !recentTransactions.isEmpty()) {
            content.append("RECENT TRANSACTIONS (Last 10)\n");
            content.append("-".repeat(50)).append("\n");
            recentTransactions.forEach(t -> {
                content.append(String.format("- %-10s | %-15s | $%-10.2f | %s%n",
                        t.getDate(),
                        t.getCategory(),
                        t.getAmount(),
                        t.getDescription()));
            });
        }

        content.append("\n").append("=".repeat(50)).append("\n");
        return content.toString();
    }

    // getters
    public User getUser() { return user; }
    public double getTotalBalance() { return totalBalance; }
    public Map<String, Double> getMonthlySummaries() { return monthlySummaries; }
    public List<Transaction> getRecentTransactions() { return recentTransactions; }
}