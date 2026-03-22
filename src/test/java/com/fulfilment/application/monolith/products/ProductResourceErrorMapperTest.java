package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import io.restassured.http.ContentType;

@QuarkusTest
public class ProductResourceErrorMapperTest {

  @Test
  public void testGetNonExistentProduct() {
      given()
        .when().get("/product/999999")
        .then()
        .statusCode(404)
        .body("code", is(404))
        .body("error", is("Product with id of 999999 does not exist."))
        .body("exceptionType", is("jakarta.ws.rs.WebApplicationException"));
  }

  @Test
  public void testCreateWithIdShouldFail() {
      Product product = new Product();
      product.id = 123L;
      product.name = "Invalid";

      given()
        .contentType(ContentType.JSON)
        .body(product)
        .when().post("/product")
        .then()
        .statusCode(422)
        .body("code", is(422))
        .body("error", is("Id was invalidly set on request."));
  }

  @Test
  public void testUpdateWithNullNameShouldFail() {
      Product product = new Product();
      // name is null

      given()
        .contentType(ContentType.JSON)
        .body(product)
        .when().put("/product/1")
        .then()
        .statusCode(422)
        .body("code", is(422))
        .body("error", is("Product Name was not set on request."));
  }

  @Test
  public void testUpdateNonExistentProduct() {
      Product product = new Product();
      product.name = "Valid Name";

      given()
        .contentType(ContentType.JSON)
        .body(product)
        .when().put("/product/999999")
        .then()
        .statusCode(404)
        .body("code", is(404))
        .body("error", is("Product with id of 999999 does not exist."));
  }

  @Test
  public void testDeleteNonExistentProduct() {
      given()
        .when().delete("/product/999999")
        .then()
        .statusCode(404)
        .body("code", is(404));
  }
}
