package com.stlcfib4.gcashapp.services;

import com.stlcfib4.gcashapp.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistory {

    public static List<String> getUserTransactions(int userId) {
        List<String> transactions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT t.transaction_id, t.amount, t.name, t.account_id, " +
                    "t.transferToID, t.transferFromID, t.date " +
                    "FROM transactions t " +
                    "WHERE t.transferToID = ? OR t.transferFromID = ? " +
                    "ORDER BY t.date DESC";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String type = (rs.getInt("transferFromID") == userId) ? "Sent" : "Received";
                String transaction = String.format(
                        "%s ₱%.2f | %s | %s",
                        type,
                        rs.getDouble("amount"),
                        rs.getString("name"),
                        rs.getTimestamp("date").toString()
                );
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}
