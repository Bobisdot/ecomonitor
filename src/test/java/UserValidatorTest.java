import org.example.UserValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    private final UserValidator validator = new UserValidator();

    @Test
    void testValidUser() {
        assertTrue(validator.isUserValid("myuser", "abc123", "test@example.com"));
    }

    @Test
    void testInvalidUsername() {
        assertFalse(validator.isUserValid("abc", "abc123", "test@example.com"));
    }

    @Test
    void testInvalidPassword_NoDigits() {
        assertFalse(validator.isUserValid("myuser", "abcdef", "test@example.com"));
    }

    @Test
    void testInvalidPassword_NoLetters() {
        assertFalse(validator.isUserValid("myuser", "123456", "test@example.com"));
    }

    @Test
    void testInvalidPassword_ShortLength() {
        assertFalse(validator.isUserValid("myuser", "a1b", "test@example.com"));
    }

    @Test
    void testInvalidEmail_NoAtSymbol() {
        assertFalse(validator.isUserValid("myuser", "abc123", "testexample.com"));
    }

    @Test
    void testInvalidEmail_NoDotCom() {
        assertFalse(validator.isUserValid("myuser", "abc123", "test@example.org"));
    }

    @Test
    void testAllInvalid() {
        assertFalse(validator.isUserValid("ab", "12", "wrong"));
    }
}
