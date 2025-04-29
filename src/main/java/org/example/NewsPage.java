package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class NewsPage extends JFrame {
    private final User currentUser;
    private final NewsDAO newsDAO;
    private final JTable newsTable;
    private final DefaultTableModel tableModel;

    public NewsPage(User currentUser) {
        this.currentUser = currentUser;
        this.newsTable = new JTable();
        this.tableModel = new DefaultTableModel(
                new Object[]{"ID", "Тақырып", "Мәтін", "Күні"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ұяшықтарды өзгертуге болмайды
            }
        };
        newsTable.setModel(tableModel);

        this.newsDAO = new NewsDAO();

        setTitle("Жаңалықтар беті - " + currentUser.username());
        setSize(800, 600);
        setLocationRelativeTo(null);

        initUI();
        loadNews();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Жаңарту батырмасы бар панель
        JButton refreshBtn = new JButton("Жаңарту");
        refreshBtn.addActionListener(e -> loadNews());
        topPanel.add(refreshBtn);

        // Админ үшін қосымша батырмалар
        if ("admin".equals(currentUser.role())) {
            JButton addNewsBtn = new JButton("Жаңалық қосу");
            JButton editNewsBtn = new JButton("Өзгерту");
            JButton deleteNewsBtn = new JButton("Жою");

            addNewsBtn.addActionListener(e -> showAddNewsDialog());
            editNewsBtn.addActionListener(e -> editSelectedNews());
            deleteNewsBtn.addActionListener(e -> deleteSelectedNews());

            topPanel.add(addNewsBtn);
            topPanel.add(editNewsBtn);
            topPanel.add(deleteNewsBtn);
        }

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(newsTable), BorderLayout.CENTER);

        add(mainPanel);
    }

    private void loadNews() {
        try {
            List<News> newsList = newsDAO.getAllNews();
            tableModel.setRowCount(0);

            for (News news : newsList) {
                tableModel.addRow(new Object[]{
                        news.id(),
                        news.title(),
                        news.content().length() > 50 ?
                                news.content().substring(0, 50) + "..." : news.content(),
                        news.createdAt().toLocalDate()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Дерекқор қатесі: " + e.getMessage(),
                    "Қате", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddNewsDialog() {
        JDialog dialog = new JDialog(this, "Жаңалық қосу", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JTextField titleField = new JTextField();
        JTextArea contentArea = new JTextArea(10, 40);
        contentArea.setLineWrap(true);

        formPanel.add(new JLabel("Тақырып:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Мәтін:"));
        formPanel.add(new JScrollPane(contentArea));

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Сақтау");
        JButton cancelBtn = new JButton("Болдырмау");

        saveBtn.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty() || contentArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Тақырып пен мәтін толтырылуы керек!",
                        "Ескерту", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                News news = new News(
                        0,
                        titleField.getText().trim(),
                        contentArea.getText().trim(),
                        LocalDateTime.now()
                );
                newsDAO.addNews(news);
                loadNews();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Қате: " + ex.getMessage(),
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

    private void editSelectedNews() {
        int selectedRow = newsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Өзгерту үшін жаңалықты таңдаңыз",
                    "Ескерту", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int newsId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            News newsToEdit = newsDAO.getNewsById(newsId);
            if (newsToEdit == null) {
                JOptionPane.showMessageDialog(this, "Жаңалық табылмады",
                        "Қате", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(this, "Жаңалықты өзгерту", true);
            dialog.setSize(500, 400);
            dialog.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
            JTextField titleField = new JTextField(newsToEdit.title());
            JTextArea contentArea = new JTextArea(newsToEdit.content(), 10, 40);
            contentArea.setLineWrap(true);

            formPanel.add(new JLabel("Тақырып:"));
            formPanel.add(titleField);
            formPanel.add(new JLabel("Мәтін:"));
            formPanel.add(new JScrollPane(contentArea));

            JPanel buttonPanel = new JPanel();
            JButton saveBtn = new JButton("Сақтау");
            JButton cancelBtn = new JButton("Болдырмау");

            saveBtn.addActionListener(e -> {
                if (titleField.getText().trim().isEmpty() || contentArea.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Тақырып пен мәтін толтырылуы керек!",
                            "Ескерту", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    News updatedNews = new News(
                            newsToEdit.id(),
                            titleField.getText().trim(),
                            contentArea.getText().trim(),
                            newsToEdit.createdAt()
                    );
                    newsDAO.updateNews(updatedNews);
                    loadNews();
                    dialog.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Қате: " + ex.getMessage(),
                            "Қате", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelBtn.addActionListener(e -> dialog.dispose());

            buttonPanel.add(saveBtn);
            buttonPanel.add(cancelBtn);

            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Қате: " + ex.getMessage(),
                    "Қате", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedNews() {
        int selectedRow = newsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Жою үшін жаңалықты таңдаңыз",
                    "Ескерту", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int newsId = (int) tableModel.getValueAt(selectedRow, 0);
        String newsTitle = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Сіз шынымен '" + newsTitle + "' жаңалығын жойғыңыз келе ме?",
                "Жоюды растау",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                newsDAO.deleteNews(newsId);
                loadNews();
                JOptionPane.showMessageDialog(this, "Жаңалық сәтті жойылды",
                        "Ақпарат", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Жою кезінде қате: " + e.getMessage(),
                        "Қате", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}