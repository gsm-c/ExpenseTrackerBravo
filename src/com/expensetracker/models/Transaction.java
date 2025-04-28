package com.expensetracker.models;

import java.util.Objects;
import java.time.LocalDate;

public class Transaction {
    private int id;
    private int userId;
    private String type;
    private String category;
    private double amount;
    private String date;
    private String description;

    public Transaction(int userId, String type, String category,
                       double amount, String date, String description) {
        this(-1, userId, type, category, amount, date, description);
    }
    // constructor
    public Transaction(int id, int userId, String type, String category,
                       double amount, String date, String description) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // getters
    public int getId() { return id; }
    public void setId( int id) { this.id = id;}
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId;}
    public String getType() { return type; }
    public void setType(String type) { this.type = type;}
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }


    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id &&
                userId == that.userId &&
                Double.compare(that.amount, amount) == 0 &&
                Objects.equals(type, that.type) &&
                Objects.equals(category, that.category) &&
                Objects.equals(date, that.date) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, type, category, amount, date, description);
    }
}
