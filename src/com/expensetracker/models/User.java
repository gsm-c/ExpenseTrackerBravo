package com.expensetracker.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;

    public User(int id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }


    public boolean isAdmin() {
        return "admin".equals(role);
    }

    // password verification
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}
