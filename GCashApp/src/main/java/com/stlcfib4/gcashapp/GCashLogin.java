package com.stlcfib4.gcashapp;

import com.stlcfib4.gcashapp.models.User;
import com.stlcfib4.gcashapp.services.UserAuthentication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GCashLogin extends JFrame {
    private JPanel MainPanel;
    private JLabel phoneLabel;
    private JTextField phoneField;
    private JPasswordField pinField;
    private JButton loginButton;
    private JLabel messageLabel;

    public GCashLogin() {
        setTitle("GCash Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        MainPanel = new JPanel();
        MainPanel.setLayout(new GridBagLayout());
        MainPanel.setBackground(new Color(0, 92, 169));
        add(MainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel logoLabel = new JLabel("GCash", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial Black", Font.BOLD, 26));
        logoLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        MainPanel.add(logoLabel, gbc);

        // Phone number field
        gbc.gridy++;
        gbc.gridwidth = 1;
        MainPanel.add(new JLabel("Phone Number:"), gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(15);
        MainPanel.add(phoneField, gbc);

        // PIN field
        gbc.gridx = 0;
        gbc.gridy++;
        MainPanel.add(new JLabel("PIN:"), gbc);

        gbc.gridx = 1;
        pinField = new JPasswordField(15);
        MainPanel.add(pinField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 153, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        MainPanel.add(loginButton, gbc);

        // Message label
        gbc.gridy++;
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        MainPanel.add(messageLabel, gbc);

        // Action listener
        loginButton.addActionListener(this::loginAction);
    }

    private void loginAction(ActionEvent e) {
        String phoneNumber = phoneField.getText().trim();
        String pin = new String(pinField.getPassword()).trim();

        UserAuthentication auth = new UserAuthentication();
        User loggedIn = auth.login(phoneNumber, pin);

        if (loggedIn != null) {
            // Use the balance already filled inside User
            double balance = loggedIn.getBalance();

            messageLabel.setText("✅ Welcome, " + loggedIn.getFullName() + "!");
            dispose();
            new GCashHome(loggedIn, balance);
        } else {
            messageLabel.setText("❌ Invalid phone number or PIN.");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GCashLogin().setVisible(true));
    }
}
