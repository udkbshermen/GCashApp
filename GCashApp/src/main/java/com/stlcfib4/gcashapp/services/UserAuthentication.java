package com.stlcfib4.gcashapp.services;

import com.stlcfib4.gcashapp.db.DatabaseConnection;
import com.stlcfib4.gcashapp.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthentication {

    /**
     * Login using phone number (column `number`) and pin (column `pin`).
     * Fetches user's balance from the balance table (`balance_id`, `user_id`, `amount`).
     */
    public User login(String phoneNumber, String pin) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Get user record using phone number and pin
            String sql = "SELECT user_id, name FROM users WHERE number = ? AND pin = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, phoneNumber);
                ps.setString(2, pin);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null; // Invalid credentials
                    }

                    int userId = rs.getInt("user_id");
                    String name = rs.getString("name");

                    // Get user's balance using user_id (from balance table)
                    double balance = 0.0;
                    String balSql = "SELECT amount FROM balance WHERE user_id = ?";
                    try (PreparedStatement balPs = conn.prepareStatement(balSql)) {
                        balPs.setInt(1, userId);
                        try (ResultSet brs = balPs.executeQuery()) {
                            if (brs.next()) {
                                balance = brs.getDouble("amount");
                            }
                        }
                    }

                    // Return User object
                    // User(int id, String phoneNumber, String pin, String fullName, double balance)
                    return new User(userId, phoneNumber, pin, name, balance);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
