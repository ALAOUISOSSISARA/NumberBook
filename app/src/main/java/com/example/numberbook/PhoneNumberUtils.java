package com.example.numberbook;

public class PhoneNumberUtils {

    /**
     * Cleans a phone number by removing spaces, dashes and parentheses.
     * Example: "+212 06-12 34 56" → "+21206123456"
     */
    public static String clean(String rawNumber) {
        if (rawNumber == null) return "";
        return rawNumber
                .replaceAll("[\\s\\-().]+", "")
                .trim();
    }

    /**
     * Basic duplicate check — compares cleaned numbers.
     */
    public static boolean isDuplicate(String incoming, java.util.List<PhoneEntry> existing) {
        String cleaned = clean(incoming);
        for (PhoneEntry entry : existing) {
            if (clean(entry.getPhoneNumber()).equals(cleaned)) {
                return true;
            }
        }
        return false;
    }
}