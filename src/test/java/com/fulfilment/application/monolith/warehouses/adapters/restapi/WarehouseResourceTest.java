package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import com.warehouse.api.beans.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseResourceTest {

  final String path = "warehouse";

  @Test
  public void testListAllWarehouses() {
    given()
        .when()
        .get(path)
        .then()
        .statusCode(200)
        .body(containsString("MWH.001"));
  }

  @Test
  public void testGetSingleWarehouse() {
    given()
        .when()
        .get(path + "/MWH.001")
        .then()
        .statusCode(200)
        .body("businessUnitCode", containsString("MWH.001"));

    given().when().get(path + "/NON-EXISTING").then().statusCode(404);
  }

  @Test
  public void testCreateWarehouse() {
    Warehouse w = new Warehouse();
    w.setBusinessUnitCode("MWH.999");
    w.setLocation("AMSTERDAM-001");
    w.setCapacity(40); // AMSTERDAM has 50 remaining (100 max - 50 existing)
    w.setStock(10);

    given()
        .contentType("application/json")
        .body(w)
        .when()
        .post(path)
        .then()
        .statusCode(200)
        .body("businessUnitCode", containsString("MWH.999"));

    // Validation failure (capacity < stock for instance, handled by usecase)
    w.setCapacity(5);
    w.setStock(20);
    w.setBusinessUnitCode("MWH.998");
    given()
        .contentType("application/json")
        .body(w)
        .when()
        .post(path)
        .then()
        .statusCode(400); // Because IllegalArgumentException maps to 400
  }

  @Test
  public void testArchiveWarehouse() {
    Warehouse w = new Warehouse();
    w.setBusinessUnitCode("MWH.EMPTY");
    w.setLocation("TILBURG-001");
    w.setCapacity(10);
    w.setStock(0);

    given().contentType("application/json").body(w).when().post(path).then().statusCode(200);
    given().when().delete(path + "/MWH.EMPTY").then().statusCode(204);
    given().when().delete(path + "/NON-EXISTING").then().statusCode(404);
  }

  @Test
  public void testReplaceWarehouse() {
    Warehouse w = new Warehouse();
    w.setLocation("TILBURG-001");
    w.setCapacity(30);
    w.setStock(15);

    given()
        .contentType("application/json")
        .body(w)
        .when()
        .post(path + "/MWH.023/replacement")
        .then()
        .statusCode(200)
        .body("capacity", org.hamcrest.Matchers.equalTo(30));

    // Validation failure (exceed capacity limits Location 100 max, 101 requested)
    w.setCapacity(200);
    w.setStock(50);
    given()
        .contentType("application/json")
        .body(w)
        .when()
        .post(path + "/MWH.023/replacement")
        .then()
        .statusCode(400);
  }
}
