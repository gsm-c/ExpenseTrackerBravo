package com.expensetracker.gui;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardPanel extends JPanel {
    private JTabbedPane tabbedPane;

    public AdminDashboardPanel() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab("User Management", createUserManagementPanel());
        tabbedPane.addTab("All Transactions", createAllTransactionsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table setup
        String[] columns = {"ID", "Username", "Role"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable usersTable = new JTable(model);

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
        String[] columns = {"ID", "User", "Type", "Category", "Amount", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable transactionsTable = new JTable(model);

        // Load data would go here (need to implement in DatabaseManager)

        panel.add(new JScrollPane(transactionsTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshUserTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<User> users = DatabaseManager.getAllUsers();
        for (User user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            });
        }
    }
}