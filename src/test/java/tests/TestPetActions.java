package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Pet;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
