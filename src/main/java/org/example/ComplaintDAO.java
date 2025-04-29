package org.example;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    public void addComplaint(Complaint complaint) throws SQLException {
        String sql = "INSERT INTO complaints (title, description, location, complaint_type, status, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, complaint.title());
            stmt.setString(2, complaint.description());
            stmt.setString(3, complaint.location());
            stmt.setString(4, complaint.complaintType());
            stmt.setString(5, complaint.status());
            stmt.setInt(6, complaint.userId());

            stmt.executeUpdate();
        }
    }


    public void deleteComplaint(int id) throws SQLException {
        String sql = "DELETE FROM complaints WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }


    public List<Complaint> getAllComplaints() throws SQLException {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                complaints.add(new Complaint(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("location"),
                        rs.getString("complaint_type"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("user_id")
                ));
            }
        }
        return complaints;
    }
}