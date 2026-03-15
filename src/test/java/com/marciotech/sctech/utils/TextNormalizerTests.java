package com.marciotech.sctech.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TextNormalizerTests {

    @Test
    void removeAcentosENormalizaEspacos() {
        assertEquals("Agrolandia", TextNormalizer.normalize("Agrolândia"));
        assertEquals("Agronomica", TextNormalizer.normalize("Agronômica"));
        assertEquals("Agua Doce", TextNormalizer.normalize("  Água   Doce  "));
        assertEquals("Sao Jose", TextNormalizer.normalize("São José"));
        assertEquals("Icara", TextNormalizer.normalize("Içara"));
        assertEquals("Braco do Norte", TextNormalizer.normalize("Braço do Norte"));
        assertEquals("", TextNormalizer.normalize("   "));
    }
}
