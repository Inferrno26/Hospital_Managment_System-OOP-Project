package projectJava;

import exceptions.ValidationException;


public class ParseUtil {
    public static String requireNonEmpty(String text, String fieldName) {
        if (text == null || text.trim().isEmpty())
            throw new ValidationException(fieldName + " is required.");
        return text.trim();
    }

    public static double parseDouble(String text, String fieldName) {
        if (text == null || text.trim().isEmpty())
            throw new ValidationException(fieldName + " is required.");
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid number.");
        }
    }
}
