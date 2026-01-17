package com.hospital.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Utility class for form input validation
 * 
 * CR-001: Add Input Validation for Patient/Doctor Forms
 * @author LI JIA QI (218451)
 * @version 1.0
 * @since 2026-01-17
 */
public class ValidationUtil {
    
    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Phone regex pattern (10-15 digits, may start with +)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[0-9]{10,15}$"
    );
    
    // Store original borders for reset
    private static final Border DEFAULT_BORDER = UIManager.getBorder("TextField.border");
    private static final Border ERROR_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 53, 69), 2),
        BorderFactory.createEmptyBorder(2, 5, 2, 5)
    );
    
    /**
     * Validate that a text field is not empty
     */
    public static String validateRequired(JTextField field, String fieldName) {
        if (field.getText().trim().isEmpty()) {
            highlightError(field);
            return fieldName + " is required.";
        }
        clearError(field);
        return null;
    }
    
    /**
     * Validate email format
     */
    public static String validateEmail(JTextField field) {
        String email = field.getText().trim();
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            highlightError(field);
            return "Please enter a valid email address (e.g., example@domain.com).";
        }
        clearError(field);
        return null;
    }
    
    /**
     * Validate phone number format
     */
    public static String validatePhone(JTextField field) {
        String phone = field.getText().trim().replaceAll("[\\s\\-()]", "");
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            highlightError(field);
            return "Please enter a valid phone number (10-15 digits).";
        }
        clearError(field);
        return null;
    }
    
    /**
     * Validate age range
     */
    public static String validateAge(JTextField field, int min, int max) {
        String ageText = field.getText().trim();
        if (ageText.isEmpty()) {
            highlightError(field);
            return "Age is required.";
        }
        
        try {
            int age = Integer.parseInt(ageText);
            if (age < min || age > max) {
                highlightError(field);
                return "Please enter a valid age between " + min + " and " + max + ".";
            }
        } catch (NumberFormatException e) {
            highlightError(field);
            return "Please enter a valid number for age.";
        }
        
        clearError(field);
        return null;
    }
    
    /**
     * Highlight field with error border
     */
    public static void highlightError(JTextField field) {
        field.setBorder(ERROR_BORDER);
        field.setBackground(new Color(255, 240, 240));
    }
    
    /**
     * Clear error highlighting from field
     */
    public static void clearError(JTextField field) {
        field.setBorder(DEFAULT_BORDER);
        field.setBackground(Color.WHITE);
    }
    
    /**
     * Clear all error highlighting from multiple fields
     */
    public static void clearAllErrors(JTextField... fields) {
        for (JTextField field : fields) {
            clearError(field);
        }
    }
}
