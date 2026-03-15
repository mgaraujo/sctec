package com.marciotech.sctech.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

public final class TextNormalizer {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private TextNormalizer() {}

    /**
     * Removes diacritics (accent marks) and normalizes whitespace.
     * Keeps casing unchanged.
     */
    public static String normalize(String input) {
        if (input == null) {
            return null;
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }

        String decomposed = Normalizer.normalize(trimmed, Form.NFD);
        String withoutAccents = DIACRITICS.matcher(decomposed).replaceAll("");
        return WHITESPACE.matcher(withoutAccents).replaceAll(" ");
    }
}

