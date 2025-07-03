package com.shimi.gogoscrum.common;

import com.shimi.gogoscrum.common.util.RandomToolkit;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomToolkitTest {
    private static final String LOWER_UPPER_CASE_ALPHABETS_AND_DIGITS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Test
    void testOneRandomString() {
        int length = 10;
        String randomString = RandomToolkit.getRandomString(length);
        assertNotNull(randomString);
        assertEquals(length, randomString.length());
        checkEachLetterValid(randomString, LOWER_UPPER_CASE_ALPHABETS_AND_DIGITS);
    }

    @Test
    void testMultipleRandomString() {
        int size = 1000;
        int stringLength = 12;
        List<String> results = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            String randomString = RandomToolkit.getRandomString(stringLength);
            assertNotNull(randomString);
            assertEquals(stringLength, randomString.length());
            checkEachLetterValid(randomString, LOWER_UPPER_CASE_ALPHABETS_AND_DIGITS);

            // Check if the generated string is unique
            if (!CollectionUtils.isEmpty(results)) {
                assertFalse(results.contains(randomString));
            }

            results.add(randomString);
        }
    }

    void checkEachLetterValid(String result, String validLetters) {
        for (int i = 0; i < result.length(); i++) {
            String letter = result.substring(i, i + 1);
            assertTrue(validLetters.contains(letter));
        }
    }
}
