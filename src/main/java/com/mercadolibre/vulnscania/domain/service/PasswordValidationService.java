package com.mercadolibre.vulnscania.domain.service;

import com.mercadolibre.vulnscania.domain.exception.WeakPasswordException;

/**
 * Domain service for password validation.
 *
 * <p>Validates password strength following OWASP password policy recommendations.
 * This is a domain service because password validation is a business rule
 * (security requirement) that doesn't depend on infrastructure concerns.</p>
 *
 * <p><strong>Password Requirements</strong>:</p>
 * <ul>
 *   <li>Minimum length: 8 characters</li>
 *   <li>Maximum length: 128 characters</li>
 *   <li>Must contain at least one uppercase letter</li>
 *   <li>Must contain at least one lowercase letter</li>
 *   <li>Must contain at least one digit</li>
 *   <li>Must contain at least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)</li>
 * </ul>
 *
 * @author Bernardo Zuluaga
 * @since 1.0.0
 */
public class PasswordValidationService {

    /**
     * Minimum password length requirement.
     */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Maximum password length limit.
     */
    private static final int MAX_PASSWORD_LENGTH = 128;

    /**
     * Validates password strength according to OWASP recommendations.
     *
     * @param password the password to validate
     * @throws WeakPasswordException if the password does not meet strength requirements
     * @throws NullPointerException if password is null
     */
    public void validatePasswordStrength(String password) {
        if (password == null || password.isBlank()) {
            throw new WeakPasswordException("Password cannot be null or blank");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new WeakPasswordException(
                String.format("Password must be at least %d characters long", MIN_PASSWORD_LENGTH)
            );
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new WeakPasswordException(
                String.format("Password must be at most %d characters long", MAX_PASSWORD_LENGTH)
            );
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (isSpecialCharacter(c)) {
                hasSpecialChar = true;
            }
        }

        if (!hasUpperCase) {
            throw new WeakPasswordException("Password must contain at least one uppercase letter");
        }

        if (!hasLowerCase) {
            throw new WeakPasswordException("Password must contain at least one lowercase letter");
        }

        if (!hasDigit) {
            throw new WeakPasswordException("Password must contain at least one digit");
        }

        if (!hasSpecialChar) {
            throw new WeakPasswordException("Password must contain at least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)");
        }
    }

    /**
     * Checks if a character is a special character allowed in passwords.
     *
     * <p>Allowed special characters: !@#$%^&*()_+-=[]{}|;:,.<>?</p>
     *
     * @param c the character to check
     * @return true if the character is an allowed special character
     */
    private boolean isSpecialCharacter(char c) {
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        return specialChars.indexOf(c) >= 0;
    }
}

