package com.expensetracker.gui;

import com.expensetracker.gui.ExpenseOverviewPanel;
import com.expensetracker.models.User;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.util.Properties;
import javax.mail.*;


public class ExpenseTrackerApp extends JFrame {
    private JDialog aboutDialog;
    private JDialog creditsDialog;
    private JDialog contactDialog;

    public ExpenseTrackerApp(User user) {

        setTitle("Expense Tracker - " + user.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // common tabs
        tabbedPane.addTab("Add Transaction", new TransactionEntryPanel(user));
        tabbedPane.addTab("View Expenses", new ExpenseOverviewPanel(user));
        tabbedPane.addTab("Monthly Review", new MonthlyReviewPanel(user));
        // admin tab
        if (user.isAdmin()) {
            tabbedPane.addTab("Admin Dashboard", new AdminDashboardPanel());
        }


        add(tabbedPane, BorderLayout.CENTER);



        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // buttons for each window
        JButton homeButton = createTextButton("Home");
        JButton aboutButton = createTextButton("About");
        JButton creditsButton = createTextButton("Credits");
        JButton contactButton = createTextButton("Contact");

        // action listeners
        homeButton.addActionListener(e -> showHomePopup());
        aboutButton.addActionListener(e -> showAboutDialog());
        creditsButton.addActionListener(e -> showCreditsDialog());
        contactButton.addActionListener(e -> showContactDialog());


        bottomPanel.add(homeButton);
        bottomPanel.add(aboutButton);
        bottomPanel.add(creditsButton);
        bottomPanel.add(contactButton);

        // social media icons
        JPanel socialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        String[] socialIcons = {"github", "linkedin", "twitter", "facebook", "instagram"};
        for (String icon : socialIcons) {
            socialPanel.add(createSocialButton(icon));
        }


        JPanel combinedBottomPanel = new JPanel(new BorderLayout());
        combinedBottomPanel.add(bottomPanel, BorderLayout.NORTH);
        combinedBottomPanel.add(socialPanel, BorderLayout.SOUTH);

        add(combinedBottomPanel, BorderLayout.SOUTH);

    }


    private JButton createSocialButton(String platform) {
        JButton button = new JButton(new ImageIcon("resources/" + platform + ".png"));
        button.setPreferredSize(new Dimension(30, 30));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(e -> openSocialLink(platform));
        return button;
    }

    private void showHomePopup() {
        if (aboutDialog == null) {
            aboutDialog = new JDialog(this, "About Project", false);
            aboutDialog.setSize(400, 300);

            JTextArea textArea = new JTextArea(
                    "Expense Tracker\n\n" +
                            "This expense tracker is made for the following goals:\n" +
                            "    * let users log their income and expenses\n" +
                            "    * give users a report of their finances\n" +
                            "    * let users visualize their expenses\n" +
                            "    * give users a monthly review of their finances\n" +
                            "    * allow users to edit and delete transaction entries\n" +
                            "    * allow admins to change user transactions\n" +
                            "    * give admins overview of entire system with respect to\n" +
                            "      users and their entries\n" +
                            "    * let admins get a combined report of a user or the\n" +
                            "      entire system\n");
            textArea.setEditable(false);
            aboutDialog.add(new JScrollPane(textArea));
        }
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }

    private void showAboutDialog() {
        JDialog dialog = new JDialog(this, "Team Qualifications", true);
        dialog.setSize(500, 400);

        JTabbedPane tabs = new JTabbedPane();
        // Add team member tabs as before
        dialog.add(tabs);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showCreditsDialog() {
        JDialog dialog = new JDialog(this, "Team Roles", true);
        dialog.setSize(600, 450);

        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        // Add team member cards as before
        dialog.add(new JScrollPane(gridPanel));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showContactDialog() {
        JDialog dialog = new JDialog(this, "Contact Us", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(this);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Message area
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Message:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea messageArea = new JTextArea(5, 20);
        messageArea.setLineWrap(true);
        formPanel.add(new JScrollPane(messageArea), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            if (validateContactForm(nameField, emailField, phoneField, messageArea)) {
                sendContactEmail(
                        nameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        messageArea.getText()
                );
                JOptionPane.showMessageDialog(dialog,
                        "Thank you for your message! We'll contact you soon.",
                        "Message Sent",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });
        buttonPanel.add(submitButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean validateContactForm(JTextField nameField, JTextField emailField,
                                        JTextField phoneField, JTextArea messageArea) {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your name",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!emailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (messageArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your message",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void sendContactEmail(String name, String email, String phone, String message) {

        String[] teamEmails = {
                "gurpaulmann@csus.edu",
                "isaiahharrell@csus.edu",
                "nbyoussef@csus.edu",
                "lilysarabia@csus.edu",
                "annetruong@csus.edu",
                "aapolonio-flores@csus.edu",
                "@csus.edu",
                "@csus.edu"
        };

        String subject = "New Contact Request from Expense Tracker App";
        String body = String.format(
                "You have received a new contact request:\n\n" +
                        "Name: %s\n" +
                        "Email: %s\n" +
                        "Phone: %s\n\n" +
                        "Message:\n%s",
                name, email, phone, message
        );


        new Thread(() -> {
            try {

                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.office365.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("expensetracker1@outlook.com", "projectbravo123");
                    }
                });

                Message emailMessage = new MimeMessage(session);
                emailMessage.setFrom(new InternetAddress("expensetracker1@outlook.com"));
                for (String toEmail : teamEmails) {
                    emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
                }
                emailMessage.setSubject(subject);
                emailMessage.setText(body);

                Transport.send(emailMessage);
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Failed to send message. Please try again later.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void openSocialLink(String platform) {
        try {
            String url = switch (platform) {
                case "github" -> "https://github.com/";
                case "linkedin" -> "https://linkedin.com/";
                case "twitter" -> "https://twitter.com/";
                case "facebook" -> "https://facebook.com/";
                case "instagram" -> "https://instagram.com/";
                default -> "#"; // fallback
            };
            Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40)); // Wider buttons
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Nice font
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(new Color(30, 144, 255));
        button.setBorderPainted(false);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}