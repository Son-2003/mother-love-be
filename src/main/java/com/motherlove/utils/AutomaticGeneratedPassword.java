package com.motherlove.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class AutomaticGeneratedPassword {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*";
    private static final String ALL_ALLOWED = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
    private static final int PASSWORD_LENGTH = 10; // Default length, can be adjusted
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomPassword(int length) {
        if (length < 8 || length > 12) {
            throw new IllegalArgumentException("Password length must be between 8 and 12 characters.");
        }

        StringBuilder password = new StringBuilder(length);

        // Ensure the password contains at least one uppercase letter, one digit, and one special character
        password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(RANDOM.nextInt(SPECIAL_CHARS.length())));

        // Fill the remaining characters with random choices from all allowed characters
        for (int i = 3; i < length; i++) {
            password.append(ALL_ALLOWED.charAt(RANDOM.nextInt(ALL_ALLOWED.length())));
        }

        // Shuffle the characters to avoid predictable patterns
        for (int i = 0; i < password.length(); i++) {
            int randomIndex = RANDOM.nextInt(password.length());
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(randomIndex));
            password.setCharAt(randomIndex, temp);
        }

        return new String(password);
    }

    // Overloaded method to use the default password length
    public static String generateRandomPassword() {
        return generateRandomPassword(PASSWORD_LENGTH);
    }
}
