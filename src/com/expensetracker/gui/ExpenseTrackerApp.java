package com.expensetracker.gui;

import com.expensetracker.gui.ExpenseOverviewPanel;
import com.expensetracker.models.User;

import javax.swing.*;

public class ExpenseTrackerApp extends JFrame {
    public ExpenseTrackerApp(User user) {
        setTitle("Expense Tracker - " + user.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Add common tabs
        tabbedPane.addTab("Add Transaction", new TransactionEntryPanel(user));
        tabbedPane.addTab("View Expenses", new ExpenseOverviewPanel(user));
        tabbedPane.addTab("Monthly Review", new MonthlyReviewPanel(user));
        // Add admin tab if user is admin
        if (user.isAdmin()) {
            tabbedPane.addTab("Admin Dashboard", new AdminDashboardPanel());
        }

        add(tabbedPane);
    }
}