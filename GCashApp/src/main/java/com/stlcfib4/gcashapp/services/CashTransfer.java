package com.stlcfib4.gcashapp.services;

import com.stlcfib4.gcashapp.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CashTransfer {

    /**
     * Transfers money from one user to another using receiver's phone number.
     *
     * @param senderId       Sender’s user ID
     * @param receiverNumber Receiver’s phone number
     * @param amount         Amount to transfer
     * @return true if successful, false otherwise
     */
    public static boolean sendMoney(int senderId, String receiverNumber, double amount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Get receiver ID + name by number
            String getReceiverQuery = "SELECT user_id, name FROM users WHERE number = ?";
            PreparedStatement receiverStmt = conn.prepareStatement(getReceiverQuery);
            receiverStmt.setString(1, receiverNumber);
            ResultSet receiverRs = receiverStmt.executeQuery();

            if (!receiverRs.next()) {
                System.out.println("❌ Receiver not found.");
                conn.rollback();
                return false;
            }

            int receiverId = receiverRs.getInt("user_id");
            String receiverName = receiverRs.getString("name");

            // 2. Get sender balance
            String checkBalanceQuery = "SELECT amount FROM balance WHERE user_id = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(checkBalanceQuery);
            balanceStmt.setInt(1, senderId);
            ResultSet balRs = balanceStmt.executeQuery();

            if (!balRs.next()) {
                System.out.println("❌ Sender balance not found.");
                conn.rollback();
                return false;
            }

            double senderBalance = balRs.getDouble("amount");
            if (senderBalance < amount) {
                System.out.println("❌ Insufficient balance.");
                conn.rollback();
                return false;
            }

            // 3. Deduct from sender
            String deductQuery = "UPDATE balance SET amount = amount - ? WHERE user_id = ?";
            PreparedStatement deductStmt = conn.prepareStatement(deductQuery);
            deductStmt.setDouble(1, amount);
            deductStmt.setInt(2, senderId);
            deductStmt.executeUpdate();

            // 4. Add to receiver
            String addQuery = "UPDATE balance SET amount = amount + ? WHERE user_id = ?";
            PreparedStatement addStmt = conn.prepareStatement(addQuery);
            addStmt.setDouble(1, amount);
            addStmt.setInt(2, receiverId);
            addStmt.executeUpdate();

            // 5. Record transaction (Java 11-safe version)
            String insertTransaction = "INSERT INTO transactions "
                    + "(amount, name, account_id, transferToID, transferFromID) "
                    + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement transStmt = conn.prepareStatement(insertTransaction);
            transStmt.setDouble(1, amount);
            transStmt.setString(2, receiverName);
            transStmt.setInt(3, senderId);
            transStmt.setInt(4, receiverId);
            transStmt.setInt(5, senderId);
            transStmt.executeUpdate();

            conn.commit();
            System.out.println("✅ Successfully sent ₱" + amount + " to " + receiverName + " (" + receiverNumber + ")");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
