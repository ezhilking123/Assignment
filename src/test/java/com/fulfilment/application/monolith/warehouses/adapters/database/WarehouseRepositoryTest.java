package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseRepositoryTest {

  @Inject WarehouseRepository warehouseRepository;

  @BeforeEach
  public void cleanUp() {
    // Left empty to not destroy import.sql data needed by other tests
  }

  @Test
  @Transactional
  public void testCreateAndGetAll() {
    Warehouse w1 = new Warehouse();
    w1.businessUnitCode = "BU-001";
    w1.location = "LOC-1";
    w1.capacity = 100;
    w1.stock = 50;

    warehouseRepository.create(w1);

    List<Warehouse> result = warehouseRepository.getAll();
    assertNotNull(result);
    // Find our created warehouse
    Warehouse found = warehouseRepository.findByBusinessUnitCode("BU-001");
    assertNotNull(found);
    assertEquals("BU-001", found.businessUnitCode);
  }

  @Test
  @Transactional
  public void testUpdate() {
    Warehouse w1 = new Warehouse();
    w1.businessUnitCode = "BU-002";
    w1.location = "LOC-2";
    w1.capacity = 100;
    w1.stock = 50;

    warehouseRepository.create(w1);

    Warehouse update = new Warehouse();
    update.businessUnitCode = "BU-002";
    update.location = "LOC-UPDATED";
    update.capacity = 200;
    update.stock = 150;

    warehouseRepository.update(update);

    Warehouse result = warehouseRepository.findByBusinessUnitCode("BU-002");
    assertNotNull(result);
    assertEquals("LOC-UPDATED", result.location);
    assertEquals(200, result.capacity);
    assertEquals(150, result.stock);
  }

  @Test
  @Transactional
  public void testUpdateNotFound() {
    Warehouse update = new Warehouse();
    update.businessUnitCode = "BU-NOT-FOUND";
    
    // Should gracefully warn and not throw exception
    warehouseRepository.update(update);
    Warehouse result = warehouseRepository.findByBusinessUnitCode("BU-NOT-FOUND");
    assertNull(result);
  }

  @Test
  @Transactional
  public void testRemove() {
    Warehouse w1 = new Warehouse();
    w1.businessUnitCode = "BU-003";
    warehouseRepository.create(w1);

    warehouseRepository.remove(w1);

    Warehouse result = warehouseRepository.findByBusinessUnitCode("BU-003");
    assertNull(result);
  }

  @Test
  @Transactional
  public void testRemoveNull() {
    warehouseRepository.remove(null);
    Warehouse empty = new Warehouse();
    warehouseRepository.remove(empty);
  }

  @Test
  @Transactional
  public void testFindByBusinessUnitCodeNotFound() {
    Warehouse result = warehouseRepository.findByBusinessUnitCode("UNKNOWN");
    assertNull(result);
  }

  @Test
  @Transactional
  public void testSearchAllParams() {
    Warehouse w1 = new Warehouse();
    w1.businessUnitCode = "SEARCH-1";
    w1.location = "PARIS";
    w1.capacity = 100;
    w1.stock = 10;
    warehouseRepository.create(w1);

    Warehouse w2 = new Warehouse();
    w2.businessUnitCode = "SEARCH-2";
    w2.location = "LONDON";
    w2.capacity = 200;
    w2.stock = 20;
    warehouseRepository.create(w2);

    // Test location, min, max
    List<Warehouse> res1 = warehouseRepository.search("PARIS", 50, 150, "capacity", "desc", 0, 10);
    assertEquals(1, res1.size());
    assertEquals("SEARCH-1", res1.get(0).businessUnitCode);

    // Test sort edge cases
    List<Warehouse> res2 = warehouseRepository.search(null, null, null, "capacity", "asc", null, null);
    org.junit.jupiter.api.Assertions.assertTrue(res2.size() >= 2);

    // Test sort edge cases with desc
    List<Warehouse> res3 = warehouseRepository.search("", null, null, "invalid_sort", "desc", null, null);
    org.junit.jupiter.api.Assertions.assertTrue(res3.size() >= 2);
    
    // Test min only
    List<Warehouse> res4 = warehouseRepository.search(null, 150, null, null, null, 0, 10);
    org.junit.jupiter.api.Assertions.assertTrue(res4.size() >= 1);

    // Test max only
    List<Warehouse> res5 = warehouseRepository.search(null, null, 150, null, null, null, null);
    org.junit.jupiter.api.Assertions.assertTrue(res5.size() >= 1);
  }
}
