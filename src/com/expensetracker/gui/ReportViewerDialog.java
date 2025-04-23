package com.expensetracker.gui;

import com.expensetracker.reports.Report;

import javax.swing.*;
import java.awt.*;

public class ReportViewerDialog extends JDialog {
    public ReportViewerDialog(Report report) {
        setTitle("Report Viewer");
        setSize(500, 400);
        setModal(true);

        JTextArea textArea = new JTextArea(report.getReportContent());
        textArea.setEditable(false);

        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}