package utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.bouncycastle.util.encoders.Base64;

import static io.restassured.RestAssured.given;

public class RestWrapper {
    PropertyManager propertyManager = new PropertyManager();

    public Response get(String url, RequestSpecification requestSpec) {
        return given()
                .spec(requestSpec)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract().response();
    }
}