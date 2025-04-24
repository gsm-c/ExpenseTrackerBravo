package com.expensetracker.gui;

import com.expensetracker.models.Transaction;
import javax.swing.*;
import java.awt.*;

public class EditTransactionDialog extends JDialog {
    private final Transaction transaction;
    private boolean saved = false;

    public EditTransactionDialog(JFrame parent, Transaction transaction) {
        super(parent, "Edit Transaction", true);
        this.transaction = transaction;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(400, 350);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        // Type
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        typeCombo.setSelectedItem(transaction.getType().substring(0, 1).toUpperCase()
                + transaction.getType().substring(1));

        // Category
        JComboBox<String> categoryCombo = new JComboBox<>(
                new String[]{"Food", "Rent", "Entertainment", "Transport", "Utilities", "Salary", "Other"});
        categoryCombo.setSelectedItem(transaction.getCategory());

        // Amount
        JTextField amountField = new JTextField(String.valueOf(transaction.getAmount()));

        // Date
        JTextField dateField = new JTextField(transaction.getDate());

        // Description
        JTextArea descriptionArea = new JTextArea(transaction.getDescription(), 3, 20);

        // Build form
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));

        // Save button
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            try {
                transaction.setType(typeCombo.getSelectedItem().toString().toLowerCase());
                transaction.setCategory(categoryCombo.getSelectedItem().toString());
                transaction.setAmount(Double.parseDouble(amountField.getText()));
                transaction.setDate(dateField.getText());
                transaction.setDescription(descriptionArea.getText());

                saved = true;
                dispose();} catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    public boolean isSaved() {
        return saved;
    }
}