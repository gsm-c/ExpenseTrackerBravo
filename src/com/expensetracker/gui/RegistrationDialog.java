package com.expensetracker.gui;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegistrationDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton loginButton;

    public RegistrationDialog(JFrame parent) {
        super(parent, "Create New Account", true);
        setSize(400, 450);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);

        // Form panel with box layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setBackground(Color.WHITE);
        formPanel.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));

        // Add form fields
        addFormField(formPanel, "Username:", usernameField = new JTextField());
        addFormField(formPanel, "Password:", passwordField = new JPasswordField());
        addFormField(formPanel, "Confirm Password:", confirmPasswordField = new JPasswordField());

        JPanel buttonPanel = new JPanel(new GridBagLayout()); // Better layout control
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);

        registerButton = new JButton("Register");
        styleButton(registerButton, new Color(70, 130, 180)); // Blue color
        registerButton.addActionListener(this::registerUser);
        buttonPanel.add(registerButton, gbc);



        // Add button panel to form
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(buttonPanel);

        // Bottom panel with login link
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(245, 245, 245));
        loginButton = new JButton("Already have an account? Login");
        styleLinkButton(loginButton);
        loginButton.addActionListener(e -> returnToLogin());
        bottomPanel.add(loginButton);

        // Add all components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        centerOnScreen();
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
        fieldPanel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldPanel.add(label, BorderLayout.NORTH);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        fieldPanel.add(field, BorderLayout.CENTER);

        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);  // THIS IS CRUCIAL
        button.setContentAreaFilled(true); // Ensure background is painted
    }

    private void styleLinkButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(new Color(70, 130, 180));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2,
                (screenSize.height - getHeight()) / 2);
    }

    private void returnToLogin() {
        dispose();
        new LoginDialog(null).setVisible(true);
    }

    private void registerUser(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (DatabaseManager.usernameExists(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create user
        if (DatabaseManager.createUser(username, password, "regular")) {
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nYou can now login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            returnToLogin();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to create account",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}