package com.rizqifauzan.api.tests;

import com.rizqifauzan.api.base.BaseTest;
import com.rizqifauzan.api.utils.RandomDataUtils;
import com.rizqifauzan.api.utils.TestDataReader;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Epic("API Automation - Siswa Management")
@Feature("Authentication")
public class AuthTests extends BaseTest {

    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";

    private String defaultPassword;

    @BeforeClass(alwaysRun = true)
    public void setupTestData() {
        defaultPassword = "Password123";
    }

    // ================== POSITIVE TEST 1 ==================
    @Test(description = "Positive Test - Register user baru dengan data valid")
    @Story("Register")
    @Severity(SeverityLevel.CRITICAL)
    public void testRegisterUser_ValidData_ShouldReturnSuccess() {
        Map<String, Object> payload = TestDataReader.readAsMap("testdata/register_payload.json");
        payload.put("email", RandomDataUtils.generateRandomEmail()); // email unik tiap run
        payload.put("password", defaultPassword);

        Response response = given()
                .spec(getRequestSpec())
                .body(payload)
                .when()
                .post(REGISTER_ENDPOINT);

        response.then().log().ifValidationFails();

        // 1) Validasi status code
        Assert.assertEquals(response.getStatusCode(), 201,
                "Status code harus 201 saat register berhasil. Body: " + response.asPrettyString());

        // 2) Validasi response payload
        // NOTE: sesuaikan path JSON ("data.email") ini dengan struktur response asli dari API
        String returnedEmail = response.jsonPath().getString("data.email");
        Assert.assertEquals(returnedEmail, payload.get("email"),
                "Email pada response harus sama dengan email yang didaftarkan");
        Assert.assertNotNull(response.jsonPath().get("data.id"),
                "Response harus mengembalikan id user yang baru dibuat");
    }

    // ================== POSITIVE TEST 2 ==================
    @Test(description = "Positive Test - Login dengan kredensial yang valid")
    @Story("Login")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginUser_ValidCredentials_ShouldReturnToken() {
        // Register user baru dulu supaya kredensial dipastikan terdaftar sebelum login
        String loginEmail = RandomDataUtils.generateRandomEmail();
        Map<String, Object> registerPayload = TestDataReader.readAsMap("testdata/register_payload.json");
        registerPayload.put("email", loginEmail);
        registerPayload.put("password", defaultPassword);

        given().spec(getRequestSpec()).body(registerPayload).when().post(REGISTER_ENDPOINT);

        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", loginEmail);
        loginPayload.put("password", defaultPassword);

        Response response = given()
                .spec(getRequestSpec())
                .body(loginPayload)
                .when()
                .post(LOGIN_ENDPOINT);

        response.then().log().ifValidationFails();

        // 1) Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Status code harus 200 saat login berhasil. Body: " + response.asPrettyString());

        // 2) Validasi response payload
        String token = response.jsonPath().getString("data.token");
        Assert.assertNotNull(token, "Response login harus mengandung token JWT");
        Assert.assertFalse(token.isEmpty(), "Token tidak boleh kosong");
    }
}
