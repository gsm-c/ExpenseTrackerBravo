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
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table setup
        String[] columns = {"ID", "Type", "Category", "Amount", "Date", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        //Table setup
        transactionsTable = new JTable(model);
        transactionsTable.setRowHeight(30);
        refreshTable();
/*
        //Button Renderer
        class ButtonRenderer extends JButton implements TableCellRenderer{
            public ButtonRenderer(){
                setOpaque(true);
            }

            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                setText((value == null) ? "" : value.toString());
                return this;
            }
        }

        //Button Editor
        class ButtonEditor extends DefaultCellEditor {
            private JButton button;
            private String label;
            private boolean isPushed;
            private int currentRow;

            public ButtonEditor(JCheckBox checkBox) {
                super(checkBox);
                button = new JButton();
                button.setOpaque(true);
                button.addActionListener(e -> fireEditingStopped());
            }
            public Component getTableCellEditorComponent(JTable table, Object value,
                                                         boolean isSelected, int row, int column) {
                label = (value == null) ? "" : value.toString();
                button.setText(label);
                currentRow = row;
                isPushed = true;
                return button;
            }

            public Object getCellEditorValue() {
                if (isPushed) {
                    int modelRow = transactionsTable.convertRowIndexToModel(currentRow);
                    int transactionId = (int) transactionsTable.getModel().getValueAt(modelRow, 0);

                    if ("Edit".equals(label)) {
                        editTransaction(transactionId);
                    } else if ("Delete".equals(label)) {
                        deleteTransaction(transactionId);
                    }
                }
                isPushed = false;
                return label;
            }
        }

        // Action column
        TableColumn actionColumn = transactionsTable.getColumnModel().getColumn(6); // Adjust index as needed
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

      /*  // Chart panel
        JPanel chartPanel = createChartPanel();

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());

        buttonPanel.add(refreshButton);

        // Components
        add(new JScrollPane(transactionsTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(chartPanel, BorderLayout.EAST);
    }

    private void editTransaction(int transactionId) {
        Transaction transaction = DatabaseManager.getTransactionById(transactionId);
        if (transaction != null) {
            JDialog editDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Edit Transaction", true);
            editDialog.setLayout(new BorderLayout());
            editDialog.setSize(400, 300);

            // Create form similar to TransactionEntryPanel but with existing values
            JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

            JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
            typeCombo.setSelectedItem(transaction.getType().substring(0, 1).toUpperCase()
                    + transaction.getType().substring(1));

       */
    }


    private JPanel createChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        Map<String, Double> expensesByCategory = DatabaseManager.getExpensesByCategory(user.getId());
        expensesByCategory.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Expense Breakdown",
                dataset,
                true, // legend
                true, // tooltips
                false // urls
        );

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
                    t.getDescription()
            });
        }
    }

    private void refreshData() {
        refreshTable();
        ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.EAST).repaint();
    }
}