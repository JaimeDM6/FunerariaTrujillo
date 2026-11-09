package com.tartanga.funerariatrujillo.Security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static String hash(char[] password) {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] derivedKey = derive(password, salt, ITERATIONS);
        return ITERATIONS + "$" + encode(salt) + "$" + encode(derivedKey);
    }

    public static boolean matches(char[] password, String encodedHash) {
        if (encodedHash == null || encodedHash.isBlank()) {
            return false;
        }

        try {
            String[] parts = encodedHash.split("\\$", -1);
            if (parts.length != 3) {
                return false;
            }
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expected = Base64.getDecoder().decode(parts[2]);
            byte[] actual = derive(password, salt, iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static byte[] derive(char[] password, byte[] salt, int iterations) {
        try {
            PBEKeySpec specification = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
            try {
                return SecretKeyFactory.getInstance(ALGORITHM)
                        .generateSecret(specification)
                        .getEncoded();
            } finally {
                specification.clearPassword();
            }
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("No se pudo inicializar el algoritmo de contraseñas", exception);
        }
    }

    private static String encode(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }
}
