package com.example.allgoods.utils;

import android.util.Patterns;

public class ValidationUtils {
    public static boolean isValidOtp(String otp) {
        return otp != null && otp.length() == 4;
    }

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // Password should be at least 6 characters
        return password != null && password.length() >= 6;
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) return false;
        String cleanNumber = cardNumber.replace(" ", "");
        if (cleanNumber.length() < 13 || cleanNumber.length() > 19) return false;

        // Luhn Algorithm
        int sum = 0;
        boolean alternate = false;
        for (int i = cleanNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cleanNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    public static boolean isValidExpiryDate(String expiryDate) {
        if (expiryDate == null || !expiryDate.matches("(0[1-9]|1[0-2])/[0-9]{2}")) return false;
        
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt("20" + parts[1]);

        java.util.Calendar now = java.util.Calendar.getInstance();
        int currentMonth = now.get(java.util.Calendar.MONTH) + 1;
        int currentYear = now.get(java.util.Calendar.YEAR);

        if (year < currentYear) return false;
        if (year == currentYear && month < currentMonth) return false;

        return true;
    }

    public static boolean isValidCVV(String cvv) {
        return cvv != null && (cvv.length() == 3 || cvv.length() == 4);
    }

    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }
}
