package com.rizqifauzan.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class untuk membaca konfigurasi environment testing
 * dari file src/test/resources/config.properties
 */
public class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new RuntimeException("config.properties tidak ditemukan di src/test/resources");
            }
            PROPERTIES.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca config.properties", e);
        }
    }

    /**
     * Mengambil value konfigurasi berdasarkan key.
     * Contoh: ConfigReader.get("base.url")
     */
    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Key '" + key + "' tidak ditemukan di config.properties");
        }
        return value;
    }
}
