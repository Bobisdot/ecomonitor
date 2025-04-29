package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AuthDialog extends JDialog {
    private final UserDAO userDAO = new UserDAO();
    private User authenticatedUser = null;

    public AuthDialog(JFrame parent) {
        super(parent, "Тіркеу/Кіру", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Кіру", createLoginPanel());
        tabbedPane.addTab("Тіркелу", createRegisterPanel());

        add(tabbedPane);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginBtn = new JButton("Кіру");

        panel.add(new JLabel("Пайдаланушы аты:"));
        panel.add(usernameField);
        panel.add(new JLabel("Құпия сөз:"));
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> {
            try {
                User user = userDAO.getUserByUsername(usernameField.getText());
                if (user != null && user.password().equals(new String(passwordField.getPassword()))) {
                    authenticatedUser = user;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Қате пайдаланушы аты немесе құпия сөз",
                            "Қате", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Дерекқор қатесі: " + ex.getMessage(),
                        "Қате", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JButton registerBtn = new JButton("Тіркелу");

        panel.add(new JLabel("Пайдаланушы аты:"));
        panel.add(usernameField);
        panel.add(new JLabel("Құпия сөз:"));
        panel.add(passwordField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel());
        panel.add(registerBtn);

        registerBtn.addActionListener(e -> {
            try {
                User newUser = new User(
                        0,
                        usernameField.getText(),
                        new String(passwordField.getPassword()),
                        emailField.getText(),
                        "user"
                );
                userDAO.addUser(newUser);
                JOptionPane.showMessageDialog(this, "Сіз сәтті тіркелдіңіз!",
                        "Тіркелу", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Тіркеу кезінде қате: " + ex.getMessage(),
                        "Қате", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}
