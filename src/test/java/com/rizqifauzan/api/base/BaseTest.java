package com.rizqifauzan.api.base;

import com.rizqifauzan.api.utils.ConfigReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

/**
 * Base class untuk semua test class.
 * Bertugas menyiapkan "environment testing": base URL, default headers,
 * logging, dan integrasi Allure - supaya setiap test class tidak perlu
 * mengulang konfigurasi yang sama (DRY principle).
 */
public class BaseTest {

    protected static String baseUrl;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        baseUrl = ConfigReader.get("base.url");
        RestAssured.baseURI = baseUrl;
    }

    /**
     * Request spec untuk endpoint yang TIDAK butuh autentikasi
     * (contoh: register, login)
     */
    protected RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured()) // agar request/response otomatis masuk ke Allure report
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    /**
     * Request spec untuk endpoint yang BUTUH autentikasi (Bearer token)
     * (contoh: semua endpoint /api/siswa)
     */
    protected RequestSpecification getAuthRequestSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + token)
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }
}
