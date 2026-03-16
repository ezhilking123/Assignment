package com.fulfilment.application.monolith.warehouses.adapters;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class WarehouseSearchIT {

    @Test
    public void testSearchByLocation() {
        given()
            .queryParam("location", "AMSTERDAM-001")
            .when().get("/warehouse/search")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("location", everyItem(equalTo("AMSTERDAM-001")));
    }
    
    @Test
    public void testSearchByCapacityRange() {
        given()
            .queryParam("minCapacity", 40)
            .queryParam("maxCapacity", 60)
            .when().get("/warehouse/search")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("capacity", everyItem(allOf(greaterThanOrEqualTo(40), lessThanOrEqualTo(60))));
    }

    @Test
    public void testPaginationAndSorting() {
        given()
            .queryParam("sortBy", "capacity")
            .queryParam("sortOrder", "desc")
            .queryParam("pageSize", 2)
            .when().get("/warehouse/search")
            .then()
            .statusCode(200)
            .body("size()", lessThanOrEqualTo(2));
    }
}
