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

        setModernLook();

        // Create and show login dialog
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            new LoginDialog(frame).setVisible(true);
        });
    }

    private static void setModernLook() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 20);
            UIManager.put("Component.arc", 20);
            UIManager.put("ProgressBar.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}