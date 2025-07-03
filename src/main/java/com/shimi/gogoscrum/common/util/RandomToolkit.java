package com.shimi.gogoscrum.common.util;

import java.util.Random;

/**
 * Utility class for generating random strings and file names.
 * This class provides methods to generate random strings of specified lengths,
 * generate random file names based on the original file name, and generate random
 * strings consisting of letters or numbers.
 */
public class RandomToolkit {
    private static final Random RANDOM = new Random();
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    public static final String LETTERS_AND_DIGITS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private RandomToolkit() {
    }

    /**
     * Generate a random string, which consists of letters (both uppercase and lowercase) and digits.
     * @param length the length of the random string to generate
     * @return a random string of the specified length
     */
    public static String getRandomString(int length) {
        return getRandom(length, LETTERS_AND_DIGITS);
    }

    /**
     * Generate a random string, which consists of letters (both uppercase and lowercase).
     * @param length the length of the random string to generate
     * @return a random string of the specified length
     */
    public static String getRandomLetters(int length) {
        return getRandom(length, LETTERS);
    }

    /**
     * Generate a random string, which consists of digits.
     * @param length the length of the random string to generate
     * @return a random string of the specified length
     */
    public static String getRandomDigits(int length) {
        return getRandom(length, DIGITS);
    }

    private static String getRandom(int length, String base) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = RANDOM.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
