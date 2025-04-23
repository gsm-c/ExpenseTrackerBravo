package com.expensetracker.reports;

import com.expensetracker.models.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class CombinedReport extends Report {
    private final int totalUsers;
    private final double systemTotalBalance;
    private final Map<String, Double> categoryDistribution;
    private final List<User> topUsers;

    public CombinedReport(int totalUsers, double systemTotalBalance,
                          Map<String, Double> categoryDistribution,
                          List<User> topUsers) {
        this.totalUsers = totalUsers;
        this.systemTotalBalance = systemTotalBalance;
        this.categoryDistribution = categoryDistribution;
        this.topUsers = topUsers;
        this.generatedDate = LocalDate.now();
        this.title = "Combined System Report";
    }

    @Override
    public String getReportContent() {
        StringBuilder content = new StringBuilder();
        content.append("=".repeat(50)).append("\n");
        content.append(title).append("\n");
        content.append("=".repeat(50)).append("\n\n");
        content.append(String.format("Generated on: %s%n%n",
                generatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        // System overview
        content.append("SYSTEM OVERVIEW\n");
        content.append("-".repeat(50)).append("\n");
        content.append(String.format("Total Users: %d%n", totalUsers));
        content.append(String.format("System-wide Balance: $%.2f%n%n", systemTotalBalance));

        // Category distribution
        if (categoryDistribution != null && !categoryDistribution.isEmpty()) {
            content.append("CATEGORY DISTRIBUTION\n");
            content.append("-".repeat(50)).append("\n");
            categoryDistribution.forEach((category, total) -> {
                content.append(String.format("- %-15s: $%.2f%n", category, total));
            });
            content.append("\n");
        }
        // Top users
        if (topUsers != null && !topUsers.isEmpty()) {
            content.append("TOP USERS BY TRANSACTION COUNT\n");
            content.append("-".repeat(50)).append("\n");
            for (int i = 0; i < topUsers.size(); i++) {
                User user = topUsers.get(i);
                content.append(String.format("%d. %s%n", i+1, user.getUsername()));
            }
        }

        content.append("\n").append("=".repeat(50)).append("\n");
        return content.toString();
    }
    // Getters
    public int getTotalUsers() { return totalUsers; }
    public double getSystemTotalBalance() { return systemTotalBalance; }
    public Map<String, Double> getCategoryDistribution() { return categoryDistribution; }
    public List<User> getTopUsers() { return topUsers; }
}