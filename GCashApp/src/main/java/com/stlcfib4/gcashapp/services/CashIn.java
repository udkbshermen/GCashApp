package com.stlcfib4.gcashapp.services;

import com.stlcfib4.gcashapp.db.DatabaseConnection;
import java.sql.*;

public class CashIn {

    public static boolean cashIn(int userId, double amount) {
        if (amount <= 0) return false;

        try (Connection conn = DatabaseConnection.getConnection()) {

            String checkSql = "SELECT amount FROM balance WHERE user_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double newBalance = rs.getDouble("amount") + amount;

                String updateSql = "UPDATE balance SET amount = ? WHERE user_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setDouble(1, newBalance);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();

            } else {
                String insertSql = "INSERT INTO balance (user_id, amount) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, userId);
                insertStmt.setDouble(2, amount);
                insertStmt.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}