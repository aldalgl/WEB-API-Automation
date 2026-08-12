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
@Feature("Siswa CRUD")
public class SiswaTests extends BaseTest {

    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String SISWA_ENDPOINT = "/api/siswa";

    private String authToken;
    private String createdSiswaId;

    /**
     * Semua endpoint /api/siswa butuh autentikasi, jadi sebelum test class ini
     * dijalankan, kita perlu register + login dulu untuk mendapatkan token.
     */
    @BeforeClass(alwaysRun = true)
    public void setupAuthenticatedUser() {
        String email = RandomDataUtils.generateRandomEmail();
        String password = "Password123!";

        Map<String, Object> registerPayload = TestDataReader.readAsMap("testdata/register_payload.json");
        registerPayload.put("email", email);
        registerPayload.put("password", password);
        given().spec(getRequestSpec()).body(registerPayload).when().post(REGISTER_ENDPOINT);

        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", email);
        loginPayload.put("password", password);

        Response loginResponse = given()
                .spec(getRequestSpec())
                .body(loginPayload)
                .when()
                .post(LOGIN_ENDPOINT);

        authToken = loginResponse.jsonPath().getString("data.token");
        Assert.assertNotNull(authToken,
                "Setup gagal: token autentikasi tidak didapat. Cek endpoint /api/auth/login. Body: "
                        + loginResponse.asPrettyString());
    }

    // ================== POSITIVE TEST 3 ==================
    @Test(priority = 1, description = "Positive Test - Membuat data siswa baru dengan payload valid")
    @Story("Create Siswa")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateSiswa_ValidPayload_ShouldReturnCreated() {
        Map<String, Object> payload = TestDataReader.readAsMap("testdata/siswa_payload.json");
        payload.put("email", RandomDataUtils.generateRandomEmail());
        payload.put("nis", RandomDataUtils.generateRandomNis()); // NIS unik tiap run
        payload.put("telepon", RandomDataUtils.generateRandomPhoneNumber());

        Response response = given()
                .spec(getAuthRequestSpec(authToken))
                .body(payload)
                .when()
                .post(SISWA_ENDPOINT);

        response.then().log().ifValidationFails();

        // 1) Validasi status code
        Assert.assertEquals(response.getStatusCode(), 201,
                "Status code harus 201 saat create siswa berhasil. Body: " + response.asPrettyString());

        // 2) Validasi response payload
        Assert.assertEquals(response.jsonPath().getString("data.nama"), payload.get("nama"),
                "Nama siswa pada response harus sama dengan payload yang dikirim");

        createdSiswaId = response.jsonPath().getString("data.id");
        Assert.assertNotNull(createdSiswaId, "Response harus mengembalikan id siswa yang baru dibuat");
    }

    // ================== POSITIVE TEST 4 ==================
    @Test(priority = 2, dependsOnMethods = "testCreateSiswa_ValidPayload_ShouldReturnCreated",
            description = "Positive Test - Mengambil detail siswa berdasarkan ID yang baru dibuat")
    @Story("Get Siswa By Id")
    @Severity(SeverityLevel.NORMAL)
    public void testGetSiswaById_ExistingId_ShouldReturnDetail() {
        Response response = given()
                .spec(getAuthRequestSpec(authToken))
                .pathParam("id", createdSiswaId)
                .when()
                .get(SISWA_ENDPOINT + "/{id}");

        response.then().log().ifValidationFails();

        // 1) Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Status code harus 200 saat mengambil detail siswa. Body: " + response.asPrettyString());

        // 2) Validasi response payload
        Assert.assertEquals(response.jsonPath().getString("data.id"), createdSiswaId,
                "ID siswa pada response harus sama dengan ID yang diminta di URL");
    }

    // ================== POSITIVE TEST 5 ==================
    @Test(priority = 3, description = "Positive Test - Mengambil daftar semua siswa")
    @Story("Get All Siswa")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllSiswa_ShouldReturnList() {
        Response response = given()
                .spec(getAuthRequestSpec(authToken))
                .when()
                .get(SISWA_ENDPOINT);

        response.then().log().ifValidationFails();

        // 1) Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200,
                "Status code harus 200 saat mengambil daftar siswa. Body: " + response.asPrettyString());

        // 2) Validasi response payload -> "data" harus berupa array/list
        Assert.assertNotNull(
                response.jsonPath().getList("data.data"),
                "Response harus memiliki field data.data berupa list siswa"
        );
    }

    // ================== NEGATIVE TEST ==================
    @Test(priority = 4, description = "Negative Test - Membuat siswa dengan field wajib kosong dan tipe data salah")
    @Story("Create Siswa - Negative")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateSiswa_InvalidPayload_ShouldReturnBadRequest() {
        // payload ini sengaja tidak punya field "nama" (wajib) dan field "kelas" bertipe number, bukan string
        Map<String, Object> invalidPayload = TestDataReader.readAsMap("testdata/siswa_invalid_payload.json");

        Response response = given()
                .spec(getAuthRequestSpec(authToken))
                .body(invalidPayload)
                .when()
                .post(SISWA_ENDPOINT);

        response.then().log().ifValidationFails();

        // 1) Validasi status code
        Assert.assertEquals(response.getStatusCode(), 400);

        // 2) Validasi response
        Assert.assertFalse(response.jsonPath().getBoolean("success"));
        String errorMessage = response.jsonPath().getString("error");

        Assert.assertNotNull(errorMessage);
        Assert.assertTrue(errorMessage.contains("Validasi gagal"));
        Assert.assertTrue(errorMessage.contains("Nama"));
        Assert.assertTrue(errorMessage.contains("telepon"));
    }
}
