package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreEndpointTest {

  final String path = "store";

  @Test
  public void testGetStores() {
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200);
  }

  @Test
  public void testGetSingleStore() {
    given().when().get(path + "/2").then().statusCode(200).body("name", containsString("KALLAX"));
    given().when().get(path + "/999").then().statusCode(404);
  }

  @Test
  public void testCreateStore() {
      Store s = new Store();
      s.name = "NEW_STORE";
      s.quantityProductsInStock = 50;

      given()
          .contentType("application/json")
          .body(s)
          .when()
          .post(path)
          .then()
          .statusCode(201)
          .body("name", containsString("NEW_STORE"));

      s.id = 1L;
      given()
          .contentType("application/json")
          .body(s)
          .when()
          .post(path)
          .then()
          .statusCode(422);
  }

  @Test
  public void testUpdateStore() {
      Store s = new Store();
      s.name = "UPDATED_STORE";
      s.quantityProductsInStock = 100;
      
      given()
          .contentType("application/json")
          .body(s)
          .when()
          .put(path + "/3")
          .then()
          .statusCode(200)
          .body("name", containsString("UPDATED_STORE"));

      Store sNoName = new Store();
      given()
          .contentType("application/json")
          .body(sNoName)
          .when()
          .put(path + "/3")
          .then()
          .statusCode(422);

      given()
          .contentType("application/json")
          .body(s)
          .when()
          .put(path + "/999")
          .then()
          .statusCode(404);
  }

  @Test
  public void testPatchStore() {
      Store s = new Store();
      s.name = "PATCHED_STORE";
      
      given()
          .contentType("application/json")
          .body(s)
          .when()
          .patch(path + "/3")
          .then()
          .statusCode(200)
          .body("name", containsString("PATCHED_STORE"));

      Store sNoName = new Store();
      given()
          .contentType("application/json")
          .body(sNoName)
          .when()
          .patch(path + "/3")
          .then()
          .statusCode(422);

      given()
          .contentType("application/json")
          .body(s)
          .when()
          .patch(path + "/999")
          .then()
          .statusCode(404);
  }

  @Test
  public void testDeleteStore() {
      given().when().delete(path + "/1").then().statusCode(204);
      given().when().delete(path + "/999").then().statusCode(404);
  }
}
