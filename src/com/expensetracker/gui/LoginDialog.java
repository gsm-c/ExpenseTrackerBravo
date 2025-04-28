package com.expensetracker.gui;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginDialog(JFrame parent) {
        super(parent, "Expense Tracker", true);
        setSize(400, 350);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // header with icon and title
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.add(new JLabel(new ImageIcon("resources/logo.png")));
        JLabel titleLabel = new JLabel("EXPENSE TRACKER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);

        // form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220)),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                )
        );
        formPanel.setBackground(Color.WHITE);

        // username field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(250, 35));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(250, 35));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // login button
        JButton loginButton = new JButton("Login");
        styleButton(loginButton, new Color(70, 130, 180));
        loginButton.addActionListener(this::performLogin);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // form editing
        formPanel.add(Box.createVerticalGlue());
        formPanel.add(userLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(passLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalGlue());


        // registration link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setBackground(new Color(245, 245, 245));
        JButton registerButton = new JButton("Create new account");
        styleLinkButton(registerButton);
        registerButton.addActionListener(e -> {
            dispose();
            new RegistrationDialog(null).setVisible(true);
        });
        registerPanel.add(registerButton);
        // add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(registerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        centerOnScreen();


    }
    private void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2,
                (screenSize.height - getHeight()) / 2);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }

    private void styleLinkButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(new Color(70, 130, 180));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = DatabaseManager.authenticate(username, password);
        if (user != null) {
            dispose();
            new ExpenseTrackerApp(user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    };

}







