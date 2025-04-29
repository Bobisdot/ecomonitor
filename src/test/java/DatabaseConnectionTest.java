import org.example.DatabaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {


    @Test
    public void testGetConnectionSuccess() throws SQLException {

        Connection connection = DatabaseConnection.getConnection();

        assertNotNull(connection, "Connection should not be null");

        assertFalse(connection.isClosed(), "Connection should be open");

        connection.close();

        assertTrue(connection.isClosed(), "Connection should be closed after calling close()");
    }


    @Test
    public void testGetConnectionWithInvalidUrl() {
        String wrongUrl = "jdbc:mysql://localhost:3306/non_existent_db";
        String wrongUser = "root";
        String wrongPassword = "";

       
        assertThrows(SQLException.class, () -> {
            DriverManager.getConnection(wrongUrl, wrongUser, wrongPassword);
        });
    }


    @Test
    public void testGetConnectionWithInvalidUser() {
        String wrongUrl = "jdbc:mysql://localhost:3306/eco_monitor";
        String wrongUser = "wrong_user";
        String wrongPassword = "wrong_password";


        assertThrows(SQLException.class, () -> {
            DriverManager.getConnection(wrongUrl, wrongUser, wrongPassword);
        });
    }


    @Test
    public void testGetConnectionWithValidCredentials() throws SQLException {
        String validUrl = "jdbc:mysql://localhost:3306/eco_monitor";
        String validUser = "root";
        String validPassword = "";


        Connection connection = DriverManager.getConnection(validUrl, validUser, validPassword);

        assertNotNull(connection, "Connection should not be null");
        connection.close();
    }
}
