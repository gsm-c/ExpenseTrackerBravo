package com.expensetracker.gui;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.models.Transaction;
import com.expensetracker.models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.toedter.calendar.JCalendar;

public class TransactionEntryPanel extends JPanel {
    private final User user;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> categoryComboBox;
    private JTextField amountField;
    private JFormattedTextField dateField;
    private JTextArea descriptionArea;
    private JButton submitButton;

    public TransactionEntryPanel(User user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Transaction type
        addFormField(formPanel, "Type:",
                typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"}));

        // Category
        addFormField(formPanel, "Category:",
                categoryComboBox = new JComboBox<>(
                        new String[]{"Food", "Rent", "Transport", "Utilities", "Entertainment", "Salary", "Other"}));

        // Amount
        addFormField(formPanel, "Amount ($):", amountField = new JTextField());

        // Date field with date picker
        addFormField(formPanel, "Date:", dateField = new JFormattedTextField());
        dateField.setValue(LocalDate.now()); // Default to today
        dateField.setColumns(10);
        JButton datePickerButton = new JButton("📅");
        datePickerButton.addActionListener(e -> showDatePicker());
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(datePickerButton, BorderLayout.EAST);
        formPanel.add(datePanel);

        // Description
        addFormField(formPanel, "Description:",
                descriptionArea = new JTextArea(3, 20));

        // Submit button
        submitButton = new JButton("Add Transaction");
        submitButton.addActionListener(this::addTransaction);

        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, String label, JComponent field) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        fieldPanel.add(new JLabel(label), BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void showDatePicker() {
        JDialog dateDialog = new JDialog();
        dateDialog.setTitle("Select Date");
        dateDialog.setModal(true);
        dateDialog.setSize(300, 250);

        JCalendar calendar = new JCalendar(); // Requires JCalendar library
        calendar.addPropertyChangeListener(e -> {
            if ("calendar".equals(e.getPropertyName())) {
                dateField.setValue(calendar.getDate());
                dateDialog.dispose();
            }
        });

        dateDialog.add(calendar);
        dateDialog.setLocationRelativeTo(this);
        dateDialog.setVisible(true);
    }

    private void addTransaction(ActionEvent e) {
        try {
            String type = typeComboBox.getSelectedItem().toString().toLowerCase();
            String category = categoryComboBox.getSelectedItem().toString();
            double amount = Double.parseDouble(amountField.getText());
            LocalDate date = (LocalDate) dateField.getValue();
            String description = descriptionArea.getText();

            if (date == null) {
                throw new IllegalArgumentException("Please select a valid date");
            }

            Transaction transaction = new Transaction(
                    user.getId(),
                    type,
                    category,
                    amount,
                    date.format(DateTimeFormatter.ISO_DATE),
                    description
            );

            if (DatabaseManager.addTransaction(transaction)) {
                JOptionPane.showMessageDialog(this,
                        "Transaction added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add transaction",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid amount",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        amountField.setText("");
        descriptionArea.setText("");
        dateField.setValue(LocalDate.now());
    }
}