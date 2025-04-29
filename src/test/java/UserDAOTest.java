import org.example.User;
import org.example.UserDAO;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }
    @BeforeEach
    void cleanUp() throws SQLException {
        userDAO.deleteUserByUsername("testuser");
    }

    @Test
    void testAddAndGetUserByUsername() throws SQLException {
        // Уникалды username жасау
        String username = "testuser_" + UUID.randomUUID();
        String password = "testpass";
        String email = "test@example.com" + UUID.randomUUID();
        String role = "user";

        // Пайдаланушыны құру
        User user = new User(0, username, password, email, role);
        userDAO.addUser(user);
        userDAO.deleteUserByUsername("testuser");


        // Базадан қайта алу
        User fetchedUser = userDAO.getUserByUsername(username);
        assertNotNull(fetchedUser);
        assertEquals(username, fetchedUser.username());
        assertEquals(password, fetchedUser.password());
        assertEquals(email, fetchedUser.email());
        assertEquals(role, fetchedUser.role());
    }
}
