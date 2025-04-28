package com.expensetracker.gui;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.models.User;
import com.expensetracker.reports.MonthlyReport;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

public class MonthlyReviewPanel extends JPanel {
    private final User user;
    private JComboBox<Month> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;

    public MonthlyReviewPanel(User user) {
        this.user = user;
        setLayout(new GridLayout(6, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // initialize components
        monthComboBox = new JComboBox<>(Month.values());
        yearComboBox = new JComboBox<>(getLastFiveYears());

        incomeLabel = new JLabel("$0.00", SwingConstants.RIGHT);
        expenseLabel = new JLabel("$0.00", SwingConstants.RIGHT);
        balanceLabel = new JLabel("$0.00", SwingConstants.RIGHT);

        JButton generateButton = new JButton("Generate Report");
        JButton refreshButton = new JButton("Refresh");

        // get current month/year
        monthComboBox.setSelectedItem(LocalDate.now().getMonth());
        yearComboBox.setSelectedItem(LocalDate.now().getYear());

        // add components
        add(new JLabel("Month:"));
        add(monthComboBox);
        add(new JLabel("Year:"));
        add(yearComboBox);
        add(new JLabel("Total Income:"));
        add(incomeLabel);
        add(new JLabel("Total Expenses:"));
        add(expenseLabel);
        add(new JLabel("Balance:"));
        add(balanceLabel);
        add(generateButton);
        add(refreshButton);

        // event handling
        monthComboBox.addActionListener(e -> updateSummary());
        yearComboBox.addActionListener(e -> updateSummary());
        refreshButton.addActionListener(e -> updateSummary());
        generateButton.addActionListener(e -> generateReport());

        // initial data load
        updateSummary();
    }

    private Integer[] getLastFiveYears() {
        int currentYear = LocalDate.now().getYear();
        return new Integer[]{currentYear, currentYear-1, currentYear-2, currentYear-3, currentYear-4};
    }

    private void updateSummary() {
        Month month = (Month) monthComboBox.getSelectedItem();
        int year = (int) yearComboBox.getSelectedItem();

        double income = DatabaseManager.getMonthlyTotal(user.getId(), "income", month.getValue(), year);
        double expenses = DatabaseManager.getMonthlyTotal(user.getId(), "expense", month.getValue(), year);
        double balance = income - expenses;

        incomeLabel.setText(String.format("$%.2f", income));
        expenseLabel.setText(String.format("$%.2f", expenses));
        balanceLabel.setText(String.format("$%.2f", balance));
    }

    private void generateReport() {
        Month month = (Month) monthComboBox.getSelectedItem();
        int year = (int) yearComboBox.getSelectedItem();

        MonthlyReport report = DatabaseManager.generateMonthlyReport(
                user.getId(),
                month.getValue(),
                year
        );

        new ReportViewerDialog(report).setVisible(true);
    }
}
