package com.expensetracker.gui;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.models.Transaction;
import com.expensetracker.models.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class TransactionEntryPanel extends JPanel {
    private final User user;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> categoryComboBox;
    private JTextField amountField;
    private JTextArea descriptionArea;
    private JButton submitButton;

    public TransactionEntryPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});
        categoryComboBox = new JComboBox<>(new String[]{"Food", "Rent", "Entertainment", "Transport", "Utilities", "Salary", "Other"});
        amountField = new JTextField();
        descriptionArea = new JTextArea(3, 20);
        submitButton = new JButton("Add Transaction");

        // Add components to form
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeComboBox);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryComboBox);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));
        formPanel.add(new JLabel("")); // Empty label for spacing
        formPanel.add(submitButton);

        add(formPanel, BorderLayout.CENTER);

        // Event handling
        submitButton.addActionListener(e -> addTransaction());
    }

    private void addTransaction() {
        try {
            String type = typeComboBox.getSelectedItem().toString().toLowerCase();
            String category = categoryComboBox.getSelectedItem().toString();
            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionArea.getText();
            String date = LocalDate.now().toString();

            Transaction transaction = new Transaction(
                    user.getId(),
                    type,
                    category,
                    amount,
                    date,
                    description
            );

            if (DatabaseManager.addTransaction(transaction)) {
                JOptionPane.showMessageDialog(this, "Transaction added successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add transaction", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        amountField.setText("");
        descriptionArea.setText("");
    }
}
