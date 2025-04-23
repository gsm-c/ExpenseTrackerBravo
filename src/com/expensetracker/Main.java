package com.expensetracker;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.gui.LoginDialog;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize database
        DatabaseManager.initializeDatabase();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show login dialog
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            new LoginDialog(frame).setVisible(true);
        });
    }
}