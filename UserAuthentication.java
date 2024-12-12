package com.quiz.models;

import com.quiz.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserAuthentication {
    public static int register() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String query = "INSERT INTO Users (email,username,password) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1,email);
            stmt.setString(2, username);
            stmt.setString(3, password);            
            stmt.executeUpdate();
            System.out.println("User registered successfully.");
            return 1;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
        return -1;
    }

    public static int login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String query = "SELECT user_id FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Login successful. Welcome back, " + username + "!");
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error logging in: " + e.getMessage());
        }
        System.out.println("Invalid username or password.");
        return -1;
    }
}