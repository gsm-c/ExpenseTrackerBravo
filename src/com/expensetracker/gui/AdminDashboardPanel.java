package com.expensetracker.gui;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.models.User;
import com.expensetracker.reports.UserReport;
import com.expensetracker.reports.Report;

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

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
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
                // Handle button click action here
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

    private JPanel createAllTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table setup
        String[] columns = {"ID", "User", "Type", "Category", "Amount", "Date", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        transactionsTable = new JTable(model);

        // Load data
        refreshTransactionsTable(model);

        panel.add(new JScrollPane(transactionsTable), BorderLayout.CENTER);
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
        // Implement transaction loading from DatabaseManager
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

        // 5. Add system summary
        reportContent.append("SYSTEM WIDE SUMMARY\n");
        reportContent.append("=".repeat(50)).append("\n");
        reportContent.append(String.format("Total Income: $%.2f%n", totalSystemIncome));
        reportContent.append(String.format("Total Expenses: $%.2f%n", totalSystemExpenses));
        reportContent.append(String.format("Net Balance: $%.2f%n%n",
                totalSystemIncome - totalSystemExpenses));

        // 6. Add category breakdown
        reportContent.append("EXPENSE CATEGORIES\n");
        reportContent.append("-".repeat(40)).append("\n");
        categoryTotals.forEach((category, total) ->
                reportContent.append(String.format("- %-15s: $%.2f%n", category, total)));

        // 7. Create and show report
        Report combinedReport = new Report() {
            @Override
            public String getReportContent() {
                return reportContent.toString();
            }
        };

        new ReportViewerDialog(combinedReport).setVisible(true);
    }


}