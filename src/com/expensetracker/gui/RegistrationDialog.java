package com.expensetracker.gui;

import com.expensetracker.database.DatabaseManager;
import com.expensetracker.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton cancelButton;

    public RegistrationDialog(JFrame parent) {
        super(parent, "Create New Account", true);
        setSize(400, 450);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setBackground(Color.WHITE);

        // Add form fields (similar to login dialog)
        usernameField = createFormField("Username:", 14);
        passwordField = createPasswordField(14);
        confirmPasswordField = createPasswordField(14);

        // Register button
        registerButton = new JButton("Register");
        styleButton(registerButton, new Color(76, 175, 80)); // Green color
        registerButton.addActionListener(this::registerUser);

        // Add components to form
        formPanel.add(createLabel("Username:"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createLabel("Password:"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(createLabel("Confirm Password:"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(registerButton);

        // Back to login link
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setBackground(new Color(245, 245, 245));
        JButton loginButton = new JButton("Back to Login");
        styleLinkButton(loginButton);
        loginButton.addActionListener(e -> returnToLogin());

        // Add all components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(loginPanel, BorderLayout.SOUTH);

        add(mainPanel);
        centerOnScreen();
    }
    private JTextField createFormField(String placeholder, int fontSize) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return field;
    }

    private JPasswordField createPasswordField(int fontSize) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return field;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        dispose(); // Close registration dialog
        new LoginDialog(null).setVisible(true); // Open new login dialog
    }

    private void registerUser(ActionEvent e) {
        // Your registration logic here
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
    }
}
    /*public RegistrationDialog(JFrame parent) {
        super(parent, "Create New Account", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));
        setResizable(false);

        // Initialize components
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        // Add components
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Confirm Password:"));
        add(confirmPasswordField);
        add(new JLabel("")); // Empty cell for spacing
        add(new JLabel(""));
        add(registerButton);
        add(cancelButton);

        // Event handlers
        registerButton.addActionListener(e -> registerUser());
        cancelButton.addActionListener(e -> dispose());

        // Make Enter key trigger registration
        getRootPane().setDefaultButton(registerButton);
    }

    private void registerUser() {
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

        // Check if username exists
        if (DatabaseManager.usernameExists(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create new user (default role is "regular")
        boolean success = DatabaseManager.createUser(username, password, "regular");

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nYou can now login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            // Close registration dialog and return to login
            returnToLogin();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to create account",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnToLogin() {
        dispose(); // Close registration dialog

        // Get the parent frame (if any)
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow != null) {
            parentWindow.dispose(); // Close any parent window
        }

        // Show new login dialog
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new LoginDialog(frame).setVisible(true);
    }
}
    */

