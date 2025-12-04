package com.ezmata.messenger.util;

public class PasswordUtil {
    public static boolean isPasswordStrong(String password) {
        if (password.length() < 8) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public static String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }

    public static boolean verifyPassword(String password, String passwordHash) {
        return passwordHash.equals(hashPassword(password));
    }
}
