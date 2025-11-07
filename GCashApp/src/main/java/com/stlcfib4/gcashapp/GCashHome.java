package com.stlcfib4.gcashapp;

import com.stlcfib4.gcashapp.models.User;
import javax.swing.*;
import java.awt.*;

public class GCashHome extends JFrame {
    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private JLabel balanceLabel;
    private JButton sendMoneyButton;
    private JButton cashInButton;
    private JButton transactionButton;
    private JButton logoutButton;

    private User user;
    private double balance;

    public GCashHome(User user, double balance) {
        this.user = user;
        this.balance = balance;

        setTitle("GCash Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(0, 80, 179)); // GCash Blue
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        welcomeLabel = new JLabel("Hello, " + user.getFullName() + "!");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        mainPanel.add(welcomeLabel);

        JLabel phoneLabel = new JLabel("Phone: " + user.getPhoneNumber());
        phoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneLabel.setForeground(Color.WHITE);
        mainPanel.add(phoneLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        balanceLabel = new JLabel("₱ " + String.format("%.2f", balance));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 32));
        balanceLabel.setForeground(Color.WHITE);
        mainPanel.add(balanceLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        sendMoneyButton = createStyledButton("Send Money");
        cashInButton = createStyledButton("Cash In");
        transactionButton = createStyledButton("Transactions");
        logoutButton = createStyledButton("Logout");

        mainPanel.add(sendMoneyButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(cashInButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(transactionButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(logoutButton);

        logoutButton.addActionListener(e -> {
            dispose();
            new GCashLogin().setVisible(true);
        });

        cashInButton.addActionListener(e -> handleCashIn());

        sendMoneyButton.addActionListener(e -> handleSendMoney());

        transactionButton.addActionListener(e -> handleViewTransactions());

        add(mainPanel);
        setVisible(true);
    }

    private void handleCashIn() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to cash in:");
        if (input != null && !input.isEmpty()) {
            try {
                double amount = Double.parseDouble(input);
                boolean success = com.stlcfib4.gcashapp.services.CashIn.cashIn(user.getId(), amount);

                if (success) {
                    balance = com.stlcfib4.gcashapp.services.CheckBalance.getBalanceByUserId(user.getId());
                    balanceLabel.setText("₱ " + String.format("%.2f", balance));
                    JOptionPane.showMessageDialog(this,
                            "✅ Cash in successful!\nNew Balance: ₱ " + String.format("%.2f", balance),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Cash in failed. Please try again.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a number.");
            }
        }
    }

    private void handleViewTransactions() {
        java.util.List<String> transactions = com.stlcfib4.gcashapp.services.TransactionHistory.getUserTransactions(user.getId());

        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No transactions found yet.", "Transaction History", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("📜 Transaction History:\n\n");
            for (String t : transactions) {
                sb.append("• ").append(t).append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setBackground(new Color(245, 245, 245));
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Your Transactions", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleSendMoney() {
        JTextField receiverField = new JTextField();
        JTextField amountField = new JTextField();
        Object[] fields = {
                "Receiver Phone Number:", receiverField,
                "Amount:", amountField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Send Money", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String receiver = receiverField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());

                if (receiver.isEmpty() || amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter valid details.");
                    return;
                }

                boolean success = com.stlcfib4.gcashapp.services.CashTransfer.sendMoney(user.getId(), receiver, amount);

                if (success) {
                    balance = com.stlcfib4.gcashapp.services.CheckBalance.getBalanceByUserId(user.getId());
                    balanceLabel.setText("₱ " + String.format("%.2f", balance));
                    JOptionPane.showMessageDialog(this,
                            "✅ ₱" + String.format("%.2f", amount) + " sent to " + receiver + " successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "❌ Transfer failed. Please check receiver number or your balance.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a number.");
            }
        }
    }

    // 🔘 Styled button factory
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(0, 80, 179));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
