package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Pet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestPetActions {
    public static final String BASE_URL = "http://5.181.109.28:9090/api/v3/";

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("ksenia miticheva")
    public void testDeleteNonexistingPet() {
        Response response = step("Send DELETE request to delete non-existing pet", () ->
                given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .when()
                .delete(BASE_URL + "pet/7777"));

        String responseBody = response.getBody().asString();

        step("Check status code to be equal 200", () ->
                assertEquals(200, response.getStatusCode(), "не совпадает полученный код, получен ответ: " + responseBody)
                );

        step("Check to receive 'Pet deleted' message in the response body", () ->
                assertEquals("Pet deleted", responseBody, "Не совпал текст ответа, получено: " + responseBody)
                );
    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("ksenia miticheva")
    public void testUpdateNonexistingPet() {
        Pet pet = new Pet();
        pet.setId(9999);
        pet.setName("Non-existent Pet");
        pet.setStatus("available");

        Response response = step("Send PUT request to update non-existing pet", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .put(BASE_URL + "pet"));

        String responseBody = response.getBody().asString();

        step("Check status code to be equal 404", () ->
                assertEquals(404, response.getStatusCode(), "не совпадает полученный код, получен ответ: " + responseBody)
        );

        step("Check to receive 'Pet not found' message in the response body", () ->
                assertEquals("Pet not found", responseBody, "Не совпал текст ответа, получено: " + responseBody)
        );
    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("ksenia miticheva")
    public void testGetNonexistingPetInfo() {
        Response response = step("Send GET request to obtain info on non-existing pet", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .when()
                        .get(BASE_URL + "pet/9999"));

        String responseBody = response.getBody().asString();

        step("Check status code to be equal 404", () ->
                assertEquals(404, response.getStatusCode(), "не совпадает полученный код, получен ответ: " + responseBody)
        );

        step("Check to receive 'Pet not found' message in the response body", () ->
                assertEquals("Pet not found", responseBody, "Не совпал текст ответа, получено: " + responseBody)
        );
    }

    @ParameterizedTest(name = "Adding a pet with status {2}")
    @CsvSource({
            "765, Kittie, available, 200",
            "787, Minnie, pending, 200",
            "798, Blacky, sold, 200",
            "987, Lily, missing, 400"
    })
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("ksenia miticheva")
    public void testAddNewPet(int id, String name, String status, int statusCode) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setStatus(status);

        Response response = step("Send POST request to create new pet", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .post(BASE_URL + "pet"));

        String responseBody = response.getBody().asString();

        step("Check status code to be equal 200", () ->
                assertEquals(statusCode, response.getStatusCode(), "Received code does not match, response received: " + responseBody)
        );

        if (statusCode == 200) {
            step("Check added pet parameters against request", () -> {
                        Pet addedPet = response.as(Pet.class);
                        assertEquals(pet.getId(), addedPet.getId(), "Received id does not match");
                        assertEquals(pet.getName(), addedPet.getName(), "Received name does not match");
                        assertEquals(pet.getStatus(), addedPet.getStatus(), "Received status does not match");
                    }
            );
        } else if (statusCode == 400) {
            step("Check response body", () ->
                    assertEquals("Invalid pet status. Valid values: [available, pending, sold]", responseBody, "Received response does not match")
            );
        } else {
            step("Reveal unexpected status code", () -> {
                    System.out.println("Received status code:" + response.getStatusCode());
                    System.out.println("Received response" + responseBody);
                    fail("Test failed, unexpected code received" + response.getStatusCode());
            });
        }
    }
}
