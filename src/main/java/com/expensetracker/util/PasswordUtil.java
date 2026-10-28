package com.expensetracker.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility for cryptographic password hashing using SHA-256 with random salt.
 * Ensures secure storage of user credentials in compliance with security requirements.
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a cryptographically secure random hexadecimal salt (16 bytes = 32 hex chars).
     * @return hex string salt
     */
    public static String generateSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        return bytesToHex(saltBytes);
    }

    /**
     * Hashes the plaintext password concatenated with the salt using SHA-256.
     * @param password Plaintext password
     * @param salt Hex salt string
     * @return Hex-encoded 64-character SHA-256 hash
     */
    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Password and salt cannot be null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = salt + password;
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies if an input plaintext password matches the stored salt and hash.
     * Uses constant-time comparison to protect against timing attacks.
     * @param plainPassword Plaintext password candidate
     * @param salt Stored salt
     * @param expectedHash Stored password hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String salt, String expectedHash) {
        if (plainPassword == null || salt == null || expectedHash == null) {
            return false;
        }
        String computedHash = hashPassword(plainPassword, salt);
        return MessageDigest.isEqual(
                computedHash.getBytes(StandardCharsets.UTF_8),
                expectedHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

