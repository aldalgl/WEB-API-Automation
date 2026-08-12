package com.rizqifauzan.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class untuk membaca data test dari file JSON
 * yang berada di src/test/resources/testdata/
 */
public class TestDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Membaca file JSON dan mengembalikannya sebagai Map<String, Object>.
     * Map baru selalu dibuat (bukan hasil parsing langsung) agar setiap test
     * bisa memodifikasi value (misalnya email/nis) tanpa mengubah file JSON aslinya.
     *
     * @param classpathFile path relatif terhadap folder resources, contoh: "testdata/siswa_payload.json"
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> readAsMap(String classpathFile) {
        try (InputStream is = TestDataReader.class.getClassLoader().getResourceAsStream(classpathFile)) {
            if (is == null) {
                throw new RuntimeException("File test data tidak ditemukan di classpath: " + classpathFile);
            }
            return new HashMap<>(MAPPER.readValue(is, Map.class));
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca/parsing file test data: " + classpathFile, e);
        }
    }
}
