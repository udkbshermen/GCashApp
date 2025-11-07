package com.stlcfib4.gcashapp.services;

import com.stlcfib4.gcashapp.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckBalance {

    public static double getBalanceByUserId(int userId) {
        double balance = 0.0;

        String query = "SELECT amount FROM balance WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                balance = rs.getDouble("amount");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching balance for user ID " + userId + ": " + e.getMessage());
        }

        return balance;
    }
}
