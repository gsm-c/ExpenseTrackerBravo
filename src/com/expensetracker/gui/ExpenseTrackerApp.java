package com.expensetracker.gui;

import com.expensetracker.gui.ExpenseOverviewPanel;
import com.expensetracker.models.User;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
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
        dialog.setSize(600, 450);

        JTabbedPane tabs = new JTabbedPane();


        tabs.addTab("Gurpaul Mann", createMemberPanel(
                "Experience:\n- Java and C++ development\n- Database management\n\nSkills:\n- Java, SQL, C++\n"
        ));

        tabs.addTab("Isaiah Harrell", createMemberPanel(
                "Experience:\n- Java and C++ development\n- Database management\n\nSkills:\n- Java, SQL, C++\n"
        ));

        tabs.addTab("Nizar Youssef", createMemberPanel(
                "Experience:\n- Java and C++ development\n- Database management\n\nSkills:\n- Java, SQL, C++\n"
        ));

        tabs.addTab("Lily Sarabia", createMemberPanel(
                "Experience:\n- Java and C++ development\n- Database management\n\nSkills:\n- Java, SQL, C++\n"
        ));

        tabs.addTab("Anne Truong", createMemberPanel(
                "Experience:\n- Java and C++ development\n- Database management\n\nSkills:\n- Java, SQL, C++\n"
        ));

        tabs.addTab("Alexis Acuna", createMemberPanel(
                "Experience:\n- Java and C++ development\n- Database management\n\nSkills:\n- Java, SQL, C++\n"
        ));
        tabs.addTab("Dakota Whidden", createMemberPanel(
                "Experience:\n- Java and C++ development\n- Database management\n\nSkills:\n- Java, SQL, C++\n"
        ));
        tabs.addTab("Alexander Flores", createMemberPanel(
                "Experience:\n- Java and C++ development\n- Database management\n\nSkills:\n- Java, SQL, C++\n"
        ));

        dialog.add(tabs);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // panel for members
    private JPanel createMemberPanel(String description) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea(description);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }


    private void showCreditsDialog() {
        JDialog dialog = new JDialog(this, "Team Roles", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());


        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        gridPanel.add(createTeamCard(
                "Dakota Whidden",
                "resources/dakota.png",
                "Project Manager",
                new String[] {
                        "• Managed team meetings",
                        "• Documented team ideas",
                        "• Helped with programming"
                }
        ));


        gridPanel.add(createTeamCard(
                "Lily Sarabia",
                "resources/lily.jpg",
                "Designer",
                new String[] {
                        "• Helped design UML",
                        "• Helped design Use-Case Diagram",
                        "• Helped with programming"
                }
        ));


        gridPanel.add(createTeamCard(
                "Alexis Acuna",
                "resources/alexis.jpg",
                "Programmer",
                new String[] {
                        "• Helped with database",
                        "• Helped with login",
                        "• Helped with GUI"
                }
        ));


        gridPanel.add(createTeamCard(
                "Nizar Youssef",
                "resources/nizar.jpg",
                "Programmer",
                new String[] {
                        "• Helped with login",
                        "• Helped with database",
                        "• Helped with programming"
                }
        ));

        gridPanel.add(createTeamCard(
                "Gurpaul Mann",
                "resources/gurpaul.png",
                "Designer",
                new String[] {
                        "• Helped with UML",
                        "• Helped with Use-Case diagram",
                        "• Helped with programming"
                }
        ));

        gridPanel.add(createTeamCard(
                "Alexander Flores",
                "resources/alexander.jpg",
                "Quality Control",
                new String[] {
                        "• Helped with login",
                        "• Helped with database",
                        "• Helped with testing"
                }
        ));

        gridPanel.add(createTeamCard(
                "Anne Truong",
                "resources/anne.jpg",
                "Programmer",
                new String[] {
                        "• Helped with login",
                        "• Helped with database",
                        "• Helped with programming"
                }
        ));

        gridPanel.add(createTeamCard(
                "Isaiah Harrell",
                "resources/isaiah.jpg",
                "Analyst",
                new String[] {
                        "• Helped with design",
                        "• Helped with database",
                        "• Helped with analysis"
                }
        ));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        dialog.add(scrollPane, BorderLayout.CENTER);

        // close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createTeamCard(String name, String imagePath, String role, String[] contributions) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        // header
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(Color.WHITE);

        // headshot
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        headerPanel.add(imageLabel, BorderLayout.WEST);

        // Name and role
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(70, 130, 180)); // Blue color

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(roleLabel);

        headerPanel.add(infoPanel, BorderLayout.CENTER);

        card.add(headerPanel, BorderLayout.NORTH);

        // Contributions list
        JPanel contributionsPanel = new JPanel();
        contributionsPanel.setLayout(new BoxLayout(contributionsPanel, BoxLayout.Y_AXIS));
        contributionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        contributionsPanel.setBackground(Color.WHITE);

        JLabel contributionsTitle = new JLabel("Key Contributions:");
        contributionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        contributionsTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        contributionsPanel.add(contributionsTitle);

        for (String contribution : contributions) {
            JLabel item = new JLabel(contribution);
            item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            item.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 0));
            contributionsPanel.add(item);
        }

        card.add(contributionsPanel, BorderLayout.CENTER);

        return card;
    }


    private ImageIcon createCircularImageIcon(String imagePath, int diameter) {
        try {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image originalImage = originalIcon.getImage();
            BufferedImage bufferedImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = bufferedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));


            g2.drawImage(originalImage.getScaledInstance(diameter, diameter, Image.SCALE_SMOOTH), 0, 0, diameter, diameter, null);
            g2.dispose();

            return new ImageIcon(bufferedImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
                "alexisacuna@csus.edu",
                "dwhidden@csus.edu"
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
                case "github" -> "https://github.com/gsm-c/ExpenseTrackerBravo/";
                case "linkedin" -> "www.linkedin.com/in/expense-tracker-a9129b363";
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