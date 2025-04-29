package com.expensetracker.database;

import com.expensetracker.models.Transaction;
import com.expensetracker.models.User;
import com.expensetracker.reports.MonthlyReport;
import com.expensetracker.reports.CombinedReport;
import com.expensetracker.reports.UserReport;
import com.expensetracker.reports.Report;

import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:expense_tracker.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL CHECK(role IN ('admin', 'user')))");

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +  // UNIQUE ensures no duplicate usernames
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL CHECK(role IN ('admin', 'user'))" +
                    ")");

            // Transactions table
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "type TEXT NOT NULL CHECK(type IN ('income', 'expense'))," +
                    "category TEXT NOT NULL," +
                    "amount REAL NOT NULL," +
                    "description TEXT," +
                    "date TEXT NOT NULL," +
                    "FOREIGN KEY(user_id) REFERENCES users(id))");
            stmt.execute("INSERT OR IGNORE INTO users(username, password, role) VALUES " +
                    "('admin', 'admin123', 'admin')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static User authenticate(String username, String password) {
        String sql = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions(user_id, type, category, amount, description, date) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transaction.getUserId());
            pstmt.setString(2, transaction.getType());
            pstmt.setString(3, transaction.getCategory());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setString(5, transaction.getDescription());
            pstmt.setString(6, transaction.getDate());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Transaction> getUserTransactions(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("date"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static Map<String, Double> getExpensesByCategory(int userId) {
        Map<String, Double> expenses = new HashMap<>();
        String sql = "SELECT category, SUM(amount) as total FROM transactions " +
                "WHERE user_id = ? AND type = 'expense' GROUP BY category";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                expenses.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public static double getMonthlyTotal(int userId, String type, int month, int year) {
        String sql = "SELECT SUM(amount) as total FROM transactions " +
                "WHERE user_id = ? AND type = ? " +
                "AND strftime('%m', date) = ? " +
                "AND strftime('%Y', date) = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            pstmt.setString(3, String.format("%02d", month)); // Ensure 2-digit month
            pstmt.setString(4, String.valueOf(year));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static List<Transaction> getRecentTransactions(int userId, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ? " +
                "ORDER BY date DESC LIMIT ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("date"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, role FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static MonthlyReport generateMonthlyReport(int userId, int month, int year) {
        double income = getMonthlyTotal(userId, "income", month, year);
        double expenses = getMonthlyTotal(userId, "expense", month, year);
        Map<String, Double> expensesByCategory = getMonthlyExpensesByCategory(userId, month, year);


        return new MonthlyReport(income, expenses, expensesByCategory, month, year);
    }

    private static Map<String, Double> getMonthlyExpensesByCategory(int userId, int month, int year) {
        Map<String, Double> expenses = new HashMap<>();
        String sql = "SELECT category, SUM(amount) as total FROM transactions " +
                "WHERE user_id = ? AND type = 'expense' " +
                "AND strftime('%m', date) = ? AND strftime('%Y', date) = ? " +
                "GROUP BY category";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, String.format("%02d", month));
            pstmt.setString(3, String.valueOf(year));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                expenses.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    public static boolean createUser(String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    public static Map<String, Double> getMonthlySummaries(int userId) {
        Map<String, Double> monthlySummaries = new LinkedHashMap<>();

        // current date info
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        Month currentMonth = now.getMonth();

        // Get data for last 6 months
        for (int i = 5; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            String monthYear = date.getMonth().toString() + " " + date.getYear();

            double income = getMonthlyTotal(userId, "income", date.getMonthValue(), date.getYear());
            double expenses = getMonthlyTotal(userId, "expense", date.getMonthValue(), date.getYear());
            double balance = income - expenses;

            monthlySummaries.put(monthYear, balance);
        }

        return monthlySummaries;
    }

    public static UserReport generateUserReport(int userId) {
        User user = getUserById(userId);
        if (user == null) return null;

        double totalBalance = getTotalBalance(userId);
        Map<String, Double> monthlySummaries = getMonthlySummaries(userId);
        List<Transaction> recentTransactions = getRecentTransactions(userId, 10); // Last 10 transactions

        return new UserReport(user, totalBalance, monthlySummaries, recentTransactions);
    }

    public static User getUserById(int userId) {
        String sql = "SELECT id, username, role FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double getTotalBalance(int userId) {
        double income = getTotal(userId, "income");
        double expenses = getTotal(userId, "expense");
        return income - expenses;
    }

    public static double getTotal(int userId, String type) {
        String sql = "SELECT SUM(amount) as total FROM transactions WHERE user_id = ? AND type = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, type);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }


    public static Transaction getTransactionById(int id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Transaction(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("date"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean updateTransaction(Transaction transaction) {
        String sql = "UPDATE transactions SET type = ?, category = ?, amount = ?, "
                + "date = ?, description = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getType());
            pstmt.setString(2, transaction.getCategory());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setString(4, transaction.getDate());
            pstmt.setString(5, transaction.getDescription());
            pstmt.setInt(6, transaction.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUserRole(int userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY date DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("date"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }







}

