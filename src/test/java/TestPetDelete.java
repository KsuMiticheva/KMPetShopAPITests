import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPetDelete {
    public static final String BASE_URL = "http://5.181.109.28:9090/api/v3/";

    @Test
    public void testDeleteNonexistingPet() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .when()
                .delete(BASE_URL + "pet/7777");

        String responseBody = response.getBody().asString();

        assertEquals(200, response.getStatusCode(), "не совпадает полученный код, получен ответ: " + responseBody);

        assertEquals("Pet deleted", responseBody, "Не совпал текст ответа, получено: " + responseBody);
    }
}
