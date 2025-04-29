import org.example.Complaint;
import org.example.ComplaintDAO;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComplaintDAOTest {

    private ComplaintDAO complaintDAO;

    @BeforeEach
    void setUp() {
        complaintDAO = new ComplaintDAO();
    }

    @Test
    void testAddAndGetAllComplaints() throws SQLException {
        Complaint complaint = new Complaint(
                0,
                "Тест шағым2",
                "Шағым сипаттамасы",
                "Алматы",
                "Ауа ластануы",
                "Pending",
                LocalDateTime.now(),
                1 // Тест қолданушы ID (бар болуы керек)
        );

        complaintDAO.addComplaint(complaint);

        List<Complaint> complaints = complaintDAO.getAllComplaints();
        assertTrue(complaints.size() > 0);

        Complaint lastComplaint = complaints.get(complaints.size() - 1);
        assertEquals("Тест шағым2", lastComplaint.title());
    }
}
