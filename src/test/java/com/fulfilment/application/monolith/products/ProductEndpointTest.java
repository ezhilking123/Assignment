package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

  @Test
  public void testCrudProduct() {
    final String path = "product";

    // List all, should have all 3 products the database has initially:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("TONSTAD"), containsString("KALLAX"), containsString("BESTÅ"));

    // Delete the TONSTAD:
    given().when().delete(path + "/1").then().statusCode(204);

    // List all, TONSTAD should be missing now:
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(not(containsString("TONSTAD")), containsString("KALLAX"), containsString("BESTÅ"));
  }

  @Test
  public void testGetSingleProduct() {
    given().when().get("/product/2").then().statusCode(200).body("name", containsString("KALLAX"));
    given().when().get("/product/999").then().statusCode(404);
  }

  @Test
  public void testCreateProduct() {
      Product p = new Product();
      p.name = "NEW_PRODUCT";
      p.price = java.math.BigDecimal.valueOf(10.0);
      p.stock = 5;

      given()
          .contentType("application/json")
          .body(p)
          .when()
          .post("/product")
          .then()
          .statusCode(201)
          .body("name", containsString("NEW_PRODUCT"));

      p.id = 1L;
      given()
          .contentType("application/json")
          .body(p)
          .when()
          .post("/product")
          .then()
          .statusCode(422);
  }

  @Test
  public void testUpdateProduct() {
      Product p = new Product();
      p.name = "UPDATED_NAME";
      
      given()
          .contentType("application/json")
          .body(p)
          .when()
          .put("/product/3")
          .then()
          .statusCode(200)
          .body("name", containsString("UPDATED_NAME"));

      Product pNoName = new Product();
      given()
          .contentType("application/json")
          .body(pNoName)
          .when()
          .put("/product/3")
          .then()
          .statusCode(422);

      given()
          .contentType("application/json")
          .body(p)
          .when()
          .put("/product/999")
          .then()
          .statusCode(404);
  }
}
