package com.rizqifauzan.api.runner;

import org.testng.TestNG;

import java.util.Collections;

/**
 * Test Runner class.
 *
 * Ini adalah cara alternatif untuk menjalankan seluruh test suite
 * (selain lewat "gradle test"), yaitu langsung klik kanan -> Run 'TestRunner.main()'
 * di IntelliJ, atau dijalankan sebagai aplikasi Java biasa.
 *
 * Runner ini membaca file testng.xml yang sudah mendefinisikan
 * class-class test mana saja yang perlu dieksekusi.
 */
public class TestRunner {

    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestSuites(Collections.singletonList("src/test/resources/testng.xml"));
        testng.setVerbose(1);
        testng.run();

        // Setelah selesai, hasil raw Allure result akan otomatis
        // tergenerate di build/allure-results (karena aspectj weaving
        // dari plugin io.qameta.allure sudah aktif melalui Gradle test task).
        // Jalankan "gradle allureReport" atau "gradle allureServe" untuk melihat report.
    }
}
