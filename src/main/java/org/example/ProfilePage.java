package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ProfilePage extends JFrame {
    private final User currentUser;
    private final UserDAO userDAO;
    private final UserValidator validator = new UserValidator();

    public ProfilePage(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();

        setTitle("Профиль - " + currentUser.username());
        setSize(500, 400);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Ақпарат панелі
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Пайдаланушы аты:");
        JLabel usernameValue = new JLabel(currentUser.username());

        JLabel emailLabel = new JLabel("Email:");
        JLabel emailValue = new JLabel(currentUser.email());

        JLabel roleLabel = new JLabel("Рөлі:");
        JLabel roleValue = new JLabel(currentUser.role());

        infoPanel.add(usernameLabel);
        infoPanel.add(usernameValue);
        infoPanel.add(emailLabel);
        infoPanel.add(emailValue);
        infoPanel.add(roleLabel);
        infoPanel.add(roleValue);

        // Өзгерту панелі
        JPanel editPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JButton changePasswordBtn = new JButton("Құпия сөзді өзгерту");

        editPanel.add(new JLabel("Жаңа құпия сөз:"));
        editPanel.add(newPasswordField);
        editPanel.add(new JLabel("Құпия сөзді растау:"));
        editPanel.add(confirmPasswordField);
        editPanel.add(new JLabel());
        editPanel.add(changePasswordBtn);

        changePasswordBtn.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Құпия сөздер сәйкес келмейді!",
                        "Қате", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!validator.isValidPassword(newPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Құпия сөз кемінде 6 таңбадан тұруы керек және әріп пен санды қамтуы керек!",
                        "Қате", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                User updatedUser = new User(
                        currentUser.id(),
                        currentUser.username(),
                        newPassword,
                        currentUser.email(),
                        currentUser.role()
                );
                userDAO.updateUser(updatedUser);
                JOptionPane.showMessageDialog(this, "Құпия сөз сәтті өзгертілді!",
                        "Сәтті", JOptionPane.INFORMATION_MESSAGE);
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Қате: " + ex.getMessage(),
                        "Қате", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(editPanel, BorderLayout.CENTER);

        add(mainPanel);
    }
}