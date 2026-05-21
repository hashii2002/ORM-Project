package org.example.serenitytherapycenterorm.Util;

import java.util.regex.Pattern;

public class ValidationUtil {
    // Regex Patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z ]{3,100}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(?:0|94|\\+94)?7[0-9]{8}$");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^[A-Za-z0-9'\\/\\s,\\.-]{5,200}$");

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidEmail(String email) {

        if (email == null || email.trim().isEmpty()) return true;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) return true;
        return ADDRESS_PATTERN.matcher(address.trim()).matches();
    }
}