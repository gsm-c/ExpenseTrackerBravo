package com.expensetracker.gui;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.models.Transaction;
import com.expensetracker.models.User;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ExpenseOverviewPanel extends JPanel {
    private final User user;
    private JTable transactionsTable;

    public ExpenseOverviewPanel(User user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Type", "Category", "Amount", "Date", "Description", "Edit", "Delete"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 6;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return (column >= 6) ? JButton.class : Object.class;
            }
        };

        transactionsTable = new JTable(model);
        transactionsTable.setRowHeight(30);
        transactionsTable.setAutoCreateRowSorter(true);

        setupActionColumns();

        add(new JScrollPane(transactionsTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        add(createChartPanel(), BorderLayout.EAST);

        refreshTable();
    }

    private void setupActionColumns() {
        TableColumn editColumn = transactionsTable.getColumnModel().getColumn(6);
        editColumn.setCellRenderer(new ButtonRenderer());
        editColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        editColumn.setMaxWidth(80);

        TableColumn deleteColumn = transactionsTable.getColumnModel().getColumn(7);
        deleteColumn.setCellRenderer(new ButtonRenderer());
        deleteColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        deleteColumn.setMaxWidth(80);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        return panel;
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            if (column == 6) {
                setBackground(new Color(70, 130, 180));
                setForeground(Color.WHITE);
                setToolTipText("Edit this transaction");
            } else {
                setBackground(new Color(220, 80, 60));
                setForeground(Color.WHITE);
                setToolTipText("Delete this transaction");
            }
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private int currentRow;
        private int currentColumn;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                fireEditingStopped();
                handleButtonAction();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            currentRow = row;
            currentColumn = column;
            return button;
        }

        private void handleButtonAction() {
            int modelRow = transactionsTable.convertRowIndexToModel(currentRow);
            int transactionId = (int) transactionsTable.getModel().getValueAt(modelRow, 0);
            boolean isEditAction = currentColumn == 6;

            if (isEditAction) {
                editTransaction(transactionId);
            } else {
                deleteTransaction(transactionId);
            }
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }

    private void editTransaction(int transactionId) {
        Transaction transaction = DatabaseManager.getTransactionById(transactionId);
        if (transaction != null) {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            EditTransactionDialog dialog = new EditTransactionDialog(
                    parentWindow instanceof JFrame ? (JFrame) parentWindow : null,
                    transaction);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                boolean success = DatabaseManager.updateTransaction(transaction);
                if (success) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Transaction updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update transaction", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteTransaction(int transactionId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this transaction?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseManager.deleteTransaction(transactionId)) {
                refreshTable();
                JOptionPane.showMessageDialog(this,
                        "Transaction deleted successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete transaction",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> expensesByCategory = DatabaseManager.getExpensesByCategory(user.getId());
        expensesByCategory.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Expense Breakdown",
                dataset,
                true,
                true,
                false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) transactionsTable.getModel();
        model.setRowCount(0);
        List<Transaction> transactions = DatabaseManager.getUserTransactions(user.getId());

        for (Transaction t : transactions) {
            model.addRow(new Object[]{
                    t.getId(),
                    t.getType(),
                    t.getCategory(),
                    String.format("$%.2f", t.getAmount()),
                    t.getDate(),
                    t.getDescription(),
                    "Edit",
                    "Delete"
            });
        }
    }

    private void refreshData() {
        refreshTable();
        remove(((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.EAST));
        add(createChartPanel(), BorderLayout.EAST);
        revalidate();
        repaint();
    }
}
