package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class EcoMonitorGUI extends JFrame {
    private final JTable complaintsTable = new JTable();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Title", "Location", "Type", "Status", "Date"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Ұяшықтарды өзгертуге болмайды
        }
    };

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final UserDAO userDAO = new UserDAO();
    private final NewsDAO newsDAO = new NewsDAO();
    private User currentUser;

    public EcoMonitorGUI() {
        // Аутентификация терезесін көрсету
        showAuthDialog();

        if (currentUser == null) {
            System.exit(0);
        }

        setTitle("Экологиялық мониторинг жүйесі - " + currentUser.username());
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadComplaints();
    }

    private void showAuthDialog() {
        JDialog authDialog = new JDialog(this, "Тіркелу/Кіру", true);
        authDialog.setSize(400, 300);
        authDialog.setLayout(new BorderLayout());
        authDialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Кіру панелі
        JPanel loginPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField loginUsername = new JTextField();
        JPasswordField loginPassword = new JPasswordField();
        JButton loginBtn = new JButton("Кіру");

        loginPanel.add(new JLabel("Пайдаланушы аты:"));
        loginPanel.add(loginUsername);
        loginPanel.add(new JLabel("Құпия сөз:"));
        loginPanel.add(loginPassword);
        loginPanel.add(new JLabel());
        loginPanel.add(loginBtn);

        loginBtn.addActionListener(e -> {
            try {
                User user = userDAO.getUserByUsername(loginUsername.getText());
                if (user != null && user.password().equals(new String(loginPassword.getPassword()))) {
                    currentUser = user;
                    authDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(authDialog, "Қате пайдаланушы аты немесе құпия сөз",
                            "Қате", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(authDialog, "Дерекқор қатесі: " + ex.getMessage(),
                        "Қате", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Тіркелу панелі
        JPanel registerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField regUsername = new JTextField();
        JPasswordField regPassword = new JPasswordField();
        JTextField regEmail = new JTextField();
        JButton registerBtn = new JButton("Тіркелу");
        UserValidator validator = new UserValidator();

        registerPanel.add(new JLabel("Пайдаланушы аты:"));
        registerPanel.add(regUsername);
        registerPanel.add(new JLabel("Құпия сөз:"));
        registerPanel.add(regPassword);
        registerPanel.add(new JLabel("Email:"));
        registerPanel.add(regEmail);
        registerPanel.add(new JLabel());
        registerPanel.add(registerBtn);

        registerBtn.addActionListener(e -> {
            String username = regUsername.getText().trim();
            String password = new String(regPassword.getPassword());
            String email = regEmail.getText().trim();

            if (!validator.isUserValid(username, password, email)) {
                JOptionPane.showMessageDialog(authDialog,
                        "Төмендегі талаптарды тексеріңіз:\n" +
                                "- Пайдаланушы аты кемінде 4 таңбадан тұруы керек\n" +
                                "- Құпия сөз кемінде 6 таңбадан тұруы керек (әріп пен сан қосылған)\n" +
                                "- Email дұрыс форматта болуы керек (example@mail.com)",
                        "Қате", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (userDAO.getUserByUsername(username) != null) {
                    JOptionPane.showMessageDialog(authDialog, "Бұл пайдаланушы аты бос емес",
                            "Қате", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User newUser = new User(0, username, password, email, "user");
                userDAO.addUser(newUser);
                JOptionPane.showMessageDialog(authDialog, "Сіз сәтті тіркелдіңіз! Кіру үшін пайдаланушы атыңызды және құпия сөзіңізді пайдаланыңыз.",
                        "Тіркелу", JOptionPane.INFORMATION_MESSAGE);
                tabbedPane.setSelectedIndex(0); // Кіру қоймасына ауысу
                regUsername.setText("");
                regPassword.setText("");
                regEmail.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(authDialog, "Тіркеу кезінде қате: " + ex.getMessage(),
                        "Қате", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabbedPane.addTab("Кіру", loginPanel);
        tabbedPane.addTab("Тіркелу", registerPanel);

        authDialog.add(tabbedPane, BorderLayout.CENTER);
        authDialog.setVisible(true);
    }

    private void initUI() {
        // Негізгі панель
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Үстіңгі панель (басқару батырмалары)
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Батырмалар
        JButton addBtn = new JButton("Жаңа шағым");
        JButton deleteBtn = new JButton("Жою");
        JButton refreshBtn = new JButton("Жаңарту");
        JButton profileBtn = new JButton("Профиль");
        JButton newsBtn = new JButton("Жаңалықтар");
        JButton logoutBtn = new JButton("Шығу (" + currentUser.username() + ")");

        // Батырмаларға әрекеттерді қосу
        addBtn.addActionListener(e -> showAddComplaintDialog());
        deleteBtn.addActionListener(e -> deleteSelectedComplaint());
        refreshBtn.addActionListener(e -> loadComplaints());
        profileBtn.addActionListener(e -> new ProfilePage(currentUser).setVisible(true));
        newsBtn.addActionListener(e -> new NewsPage(currentUser).setVisible(true));
        logoutBtn.addActionListener(e -> logout());

        // Тек админдер ғана жоюға құқылы болуы үшін
        if (!"admin".equals(currentUser.role())) {
            deleteBtn.setEnabled(false);
            deleteBtn.setToolTipText("Тек админ жоюға құқылы");
        }

        // Батырмаларды панельге қосу
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(profileBtn);
        buttonPanel.add(newsBtn);

        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        // Кесте панелі
        complaintsTable.setModel(tableModel);
        complaintsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        complaintsTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(complaintsTable);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Шынымен жүйеден шығасыз ба?",
                "Шығуды растау",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            currentUser = null;
            dispose();
            new EcoMonitorGUI().setVisible(true);
        }
    }

    private void loadComplaints() {
        try {
            List<Complaint> complaints = complaintDAO.getAllComplaints();
            tableModel.setRowCount(0);

            for (Complaint complaint : complaints) {
                tableModel.addRow(new Object[]{
                        complaint.id(),
                        complaint.title(),
                        complaint.location(),
                        complaint.complaintType(),
                        complaint.status(),
                        complaint.createdAt().toLocalDate()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Дерекқор қатесі: " + e.getMessage(),
                    "Қате", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddComplaintDialog() {
        JDialog dialog = new JDialog(this, "Жаңа шағым қосу", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField();
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        JTextField locationField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
                "Ауа ластануы", "Су ластануы", "Қатты қалдықтар", "Шу ластануы", "Бақша шаруашылығы"
        });

        formPanel.add(new JLabel("Тақырып:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Сипаттама:"));
        formPanel.add(new JScrollPane(descArea));
        formPanel.add(new JLabel("Орналасқан жері:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Шағым түрі:"));
        formPanel.add(typeCombo);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Сақтау");
        JButton cancelBtn = new JButton("Болдырмау");

        saveBtn.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty() ||
                    descArea.getText().trim().isEmpty() ||
                    locationField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "Барлық өрістерді толтырыңыз!",
                        "Ескерту", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Complaint complaint = new Complaint(
                    0,
                    titleField.getText().trim(),
                    descArea.getText().trim(),
                    locationField.getText().trim(),
                    (String) typeCombo.getSelectedItem(),
                    "Pending",
                    LocalDateTime.now(),
                    currentUser.id()
            );

            try {
                complaintDAO.addComplaint(complaint);
                loadComplaints();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Шағым сәтті қосылды!",
                        "Сәтті", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Сақтау кезінде қате: " + ex.getMessage(),
                        "Қате", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedComplaint() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Жою үшін бір шағымды таңдаңыз",
                    "Ескерту", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int complaintId = (int) tableModel.getValueAt(selectedRow, 0);
        String complaintTitle = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Сіз шынымен '" + complaintTitle + "' шағымды жойғыңыз келе ме?",
                "Жоюды растау",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                complaintDAO.deleteComplaint(complaintId);
                loadComplaints();
                JOptionPane.showMessageDialog(this, "Шағым сәтті жойылды",
                        "Ақпарат", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Жою кезінде қате: " + e.getMessage(),
                        "Қате", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new EcoMonitorGUI().setVisible(true);
        });
    }
}