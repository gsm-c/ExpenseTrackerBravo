package com.expensetracker.gui;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.models.User;
import com.expensetracker.reports.UserReport;
import com.expensetracker.reports.Report;
import com.expensetracker.models.Transaction;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

public class AdminDashboardPanel extends JPanel {
    private JTabbedPane tabbedPane;
    private JTable usersTable;
    private JTable transactionsTable;

    public AdminDashboardPanel() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("All Transactions", createAllTransactionsPanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());

            // Style the button
            if ("Edit/Delete".equals(value)) {
                setBackground(new Color(70, 130, 180)); // Blue
                setForeground(Color.WHITE);
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            return this;
        }
    }

    private void editUser(int userId) {
        User user = DatabaseManager.getUserById(userId);
        if (user != null) {
            JDialog editDialog = new JDialog((Frame)null, "Edit User", true);
            editDialog.setSize(300, 200);
            editDialog.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Username field (read-only)
            formPanel.add(new JLabel("Username:"));
            JTextField usernameField = new JTextField(user.getUsername());
            usernameField.setEditable(false);
            formPanel.add(usernameField);

            // Role selection
            formPanel.add(new JLabel("Role:"));
            JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "User"});
            roleCombo.setSelectedItem(user.getRole());
            formPanel.add(roleCombo);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                String newRole = (String) roleCombo.getSelectedItem();
                if (DatabaseManager.updateUserRole(userId, newRole)) {
                    JOptionPane.showMessageDialog(editDialog,
                            "User updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    editDialog.dispose();
                    refreshUserTable((DefaultTableModel) usersTable.getModel());
                } else {
                    JOptionPane.showMessageDialog(editDialog,
                            "Failed to update user",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(saveButton);

            editDialog.add(formPanel, BorderLayout.CENTER);
            editDialog.add(buttonPanel, BorderLayout.SOUTH);
            editDialog.setVisible(true);
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {

                int selectedRow = usersTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int userId = (int) usersTable.getValueAt(selectedRow, 0);
                    editUser(userId);
                }
            }
            isPushed = false;
            return label;
        }
    }


    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table setup
        String[] columns = {"ID", "Username", "Role", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only actions column is editable
            }
        };
        usersTable = new JTable(model);
        usersTable.setRowHeight(30);

        // action buttons
        TableColumn actionsColumn = usersTable.getColumnModel().getColumn(3);
        actionsColumn.setCellRenderer(new ButtonRenderer());
        actionsColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        actionsColumn.setPreferredWidth(100);

        // Load data
        refreshUserTable(model);

        // Load data
        List<User> users = DatabaseManager.getAllUsers();
        for (User user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            });
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshUserTable(model));

        buttonPanel.add(refreshButton);

        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private class TransactionButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public TransactionButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            selectedRow = table.convertRowIndexToModel(row);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // popup menu for Edit/Delete
                JPopupMenu popup = new JPopupMenu();
                JMenuItem editItem = new JMenuItem("Edit");
                JMenuItem deleteItem = new JMenuItem("Delete");

                editItem.addActionListener(e -> {
                    editTransaction(selectedRow);
                    fireEditingStopped();
                });

                deleteItem.addActionListener(e -> {
                    deleteTransaction(selectedRow);
                    fireEditingStopped();
                });

                popup.add(editItem);
                popup.add(deleteItem);

                // popup near the button
                popup.show(button, button.getWidth()/2, button.getHeight()/2);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private JPanel createAllTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table setup
        String[] columns = {"ID", "User", "Type", "Category", "Amount", "Date", "Description", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        transactionsTable = new JTable(model) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        transactionsTable.setRowHeight(30);

        // action buttons column
        TableColumn actionsColumn = transactionsTable.getColumnModel().getColumn(7);
        actionsColumn.setCellRenderer(new ButtonRenderer());
        actionsColumn.setCellEditor(new TransactionButtonEditor(new JCheckBox()));
        actionsColumn.setPreferredWidth(100);

        // Load data
        refreshTransactionsTable(model, -1); // -1 for all users

        // filter controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> userFilter = new JComboBox<>();
        JButton applyFilter = new JButton("Filter");

        // Populate user filter
        List<User> users = DatabaseManager.getAllUsers();
        userFilter.addItem("All Users");
        for (User user : users) {
            userFilter.addItem(user.getUsername() + " (ID: " + user.getId() + ")");
        }

        filterPanel.add(new JLabel("Filter by User:"));
        filterPanel.add(userFilter);
        filterPanel.add(applyFilter);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(transactionsTable), BorderLayout.CENTER);

        // Filter button action
        applyFilter.addActionListener(e -> {
            String selected = (String) userFilter.getSelectedItem();
            int userId = selected.equals("All Users") ? -1 :
                    Integer.parseInt(selected.split("\\(ID: ")[1].replace(")", ""));
            refreshTransactionsTable(model, userId);
        });

        return panel;
    }



    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Individual user report
        JButton userReportButton = new JButton("Generate User Report");
        userReportButton.addActionListener(this::generateUserReport);

        // Combined report
        JButton combinedReportButton = new JButton("Generate Combined Report");
        combinedReportButton.addActionListener(e -> generateCombinedReport(e));

        panel.add(userReportButton);
        panel.add(combinedReportButton);

        return panel;
    }

    private void refreshUserTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<User> users = DatabaseManager.getAllUsers();
        for (User user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    "View/Edit" // Action button text
            });
        }
    }

    private void refreshTransactionsTable(DefaultTableModel model) {
        model.setRowCount(0);

    }

    private void generateUserReport(ActionEvent e) {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user first by clicking on a row",
                    "No User Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int modelRow = usersTable.convertRowIndexToModel(selectedRow);
            int userId = (int) usersTable.getModel().getValueAt(modelRow, 0);

            // Show loading indicator
            JDialog loadingDialog = new JDialog();
            loadingDialog.setUndecorated(true);
            loadingDialog.add(new JLabel("Generating report...", SwingConstants.CENTER));
            loadingDialog.setSize(200, 50);
            loadingDialog.setLocationRelativeTo(this);
            loadingDialog.setVisible(true);

            // Generate report in background
            new SwingWorker<UserReport, Void>() {
                @Override
                protected UserReport doInBackground() throws Exception {
                    return DatabaseManager.generateUserReport(userId);
                }

                @Override
                protected void done() {
                    loadingDialog.dispose();
                    try {
                        UserReport report = get();
                        if (report != null) {
                            new ReportViewerDialog(report).setVisible(true);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AdminDashboardPanel.this,
                                "Error generating report: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Report Generation Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateCombinedReport(ActionEvent e) {
        // 1. Get all users from database
        List<User> allUsers = DatabaseManager.getAllUsers();
        if (allUsers.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No users found in the system",
                    "No Data",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 2. Prepare report data
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("COMBINED FINANCIAL REPORT\n");
        reportContent.append("=".repeat(50)).append("\n\n");
        reportContent.append(String.format("Generated on: %s%n%n", LocalDate.now()));

        // 3. System-wide totals
        double totalSystemIncome = 0;
        double totalSystemExpenses = 0;
        Map<String, Double> categoryTotals = new HashMap<>();

        // 4. Process each user's data
        for (User user : allUsers) {
            // Get user's financial summary
            double userIncome = DatabaseManager.getTotal(user.getId(), "income");
            double userExpenses = DatabaseManager.getTotal(user.getId(), "expense");
            double userBalance = userIncome - userExpenses;

            // Add to system totals
            totalSystemIncome += userIncome;
            totalSystemExpenses += userExpenses;

            // Get user's expense categories
            Map<String, Double> userCategories = DatabaseManager.getExpensesByCategory(user.getId());
            userCategories.forEach((category, amount) ->
                    categoryTotals.merge(category, amount, Double::sum));

            // Add user section to report
            reportContent.append(String.format("USER: %s (ID: %d)%n", user.getUsername(), user.getId()));
            reportContent.append("-".repeat(40)).append("\n");
            reportContent.append(String.format("Income: $%.2f%n", userIncome));
            reportContent.append(String.format("Expenses: $%.2f%n", userExpenses));
            reportContent.append(String.format("Balance: $%.2f%n%n", userBalance));
        }

        // system summary
        reportContent.append("SYSTEM WIDE SUMMARY\n");
        reportContent.append("=".repeat(50)).append("\n");
        reportContent.append(String.format("Total Income: $%.2f%n", totalSystemIncome));
        reportContent.append(String.format("Total Expenses: $%.2f%n", totalSystemExpenses));
        reportContent.append(String.format("Net Balance: $%.2f%n%n",
                totalSystemIncome - totalSystemExpenses));

        // category breakdown
        reportContent.append("EXPENSE CATEGORIES\n");
        reportContent.append("-".repeat(40)).append("\n");
        categoryTotals.forEach((category, total) ->
                reportContent.append(String.format("- %-15s: $%.2f%n", category, total)));

        // report
        Report combinedReport = new Report() {
            @Override
            public String getReportContent() {
                return reportContent.toString();
            }
        };

        new ReportViewerDialog(combinedReport).setVisible(true);
    }

    private void refreshTransactionsTable(DefaultTableModel model, int userId) {
        model.setRowCount(0);
        List<Transaction> transactions = userId == -1 ?
                DatabaseManager.getAllTransactions() :
                DatabaseManager.getUserTransactions(userId);

        for (Transaction t : transactions) {
            User user = DatabaseManager.getUserById(t.getUserId());
            String username = user != null ? user.getUsername() : "Unknown";

            model.addRow(new Object[]{
                    t.getId(),
                    username + " (ID: " + t.getUserId() + ")",
                    t.getType(),
                    t.getCategory(),
                    t.getAmount(),
                    t.getDate(),
                    t.getDescription(),
                    "Edit/Delete" // Action button text
            });
        }
    }

    private void editTransaction(int rowIndex) {
        DefaultTableModel model = (DefaultTableModel) transactionsTable.getModel();
        int transactionId = (int) model.getValueAt(rowIndex, 0);

        Transaction transaction = DatabaseManager.getTransactionById(transactionId);
        if (transaction == null) return;

        // edit dialog
        JDialog editDialog = new JDialog();
        editDialog.setTitle("Edit Transaction #" + transactionId);
        editDialog.setSize(450, 350);
        editDialog.setLayout(new BorderLayout());
        editDialog.setModal(true);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Type:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        typeCombo.setSelectedItem(transaction.getType().substring(0, 1).toUpperCase() +
                transaction.getType().substring(1));
        formPanel.add(typeCombo, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> categoryCombo = new JComboBox<>(
                new String[]{"Food", "Rent", "Transport", "Utilities", "Entertainment", "Salary", "Other"});
        categoryCombo.setSelectedItem(transaction.getCategory());
        formPanel.add(categoryCombo, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        JTextField amountField = new JTextField(String.valueOf(transaction.getAmount()));
        formPanel.add(amountField, gbc);

        // Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        JTextField dateField = new JTextField(transaction.getDate());
        formPanel.add(dateField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridheight = 2;
        JTextArea descArea = new JTextArea(transaction.getDescription(), 3, 20);
        formPanel.add(new JScrollPane(descArea), gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                transaction.setType(((String) typeCombo.getSelectedItem()).toLowerCase());
                transaction.setCategory((String) categoryCombo.getSelectedItem());
                transaction.setAmount(Double.parseDouble(amountField.getText()));
                transaction.setDate(dateField.getText());
                transaction.setDescription(descArea.getText());

                if (DatabaseManager.updateTransaction(transaction)) {
                    JOptionPane.showMessageDialog(editDialog,
                            "Transaction updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTransactionsTable(model, -1); // Refresh with all users
                    editDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(editDialog,
                            "Failed to update transaction",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog,
                        "Please enter a valid amount",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> editDialog.dispose());
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        editDialog.add(formPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    private void deleteTransaction(int rowIndex) {
        DefaultTableModel model = (DefaultTableModel) transactionsTable.getModel();
        int transactionId = (int) model.getValueAt(rowIndex, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this transaction?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseManager.deleteTransaction(transactionId)) {
                JOptionPane.showMessageDialog(this,
                        "Transaction deleted successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTransactionsTable(model, -1); // Refresh with all users
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete transaction",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


}