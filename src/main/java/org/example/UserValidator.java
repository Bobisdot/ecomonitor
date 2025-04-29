package org.example;

public class UserValidator {

    public boolean isValidUsername(String username) {
        return username != null && username.length() >= 4;
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) return false;

        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        return hasLetter && hasDigit;
    }

    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.endsWith(".com");
    }

    public boolean isUserValid(String username, String password, String email) {
        return isValidUsername(username) &&
                isValidPassword(password) &&
                isValidEmail(email);
    }
}
