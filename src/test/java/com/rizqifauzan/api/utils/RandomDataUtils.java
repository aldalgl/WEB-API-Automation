package com.rizqifauzan.api.utils;

import java.util.UUID;

/**
 * Utility class untuk generate data unik/random saat runtime.
 * Ini penting supaya test bisa dijalankan berulang kali tanpa
 * bentrok data (misalnya error 409 email sudah terdaftar).
 */
import java.util.concurrent.ThreadLocalRandom;

public class RandomDataUtils {

    public static String generateRandomEmail() {
        String unique = String.valueOf(System.currentTimeMillis());
        return "qa.automation." + unique + "@mailinator.com";
    }

    public static String generateRandomNis() {
        StringBuilder nis = new StringBuilder();

        // Digit pertama tidak boleh 0
        nis.append(ThreadLocalRandom.current().nextInt(1, 10));

        for (int i = 1; i < 10; i++) {
            nis.append(ThreadLocalRandom.current().nextInt(10));
        }

        return nis.toString();
    }

    public static String generateRandomPhoneNumber() {
        StringBuilder telepon = new StringBuilder("08");

        // Tambahkan 10 digit lagi sehingga total menjadi 12 digit
        for (int i = 0; i < 10; i++) {
            telepon.append(ThreadLocalRandom.current().nextInt(10));
        }

        return telepon.toString();
    }
}
