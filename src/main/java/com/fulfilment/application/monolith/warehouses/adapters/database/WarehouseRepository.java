package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    DbWarehouse dbWarehouse = new DbWarehouse();
    dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt = warehouse.createdAt;
    dbWarehouse.archivedAt = warehouse.archivedAt;
    
    this.persist(dbWarehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
    DbWarehouse dbWarehouse = find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (dbWarehouse != null) {
      dbWarehouse.location = warehouse.location;
      dbWarehouse.capacity = warehouse.capacity;
      dbWarehouse.stock = warehouse.stock;
      dbWarehouse.archivedAt = warehouse.archivedAt;
      persist(dbWarehouse);
    }
  }

  @Override
  public void remove(Warehouse warehouse) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'remove'");
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse dbWarehouse = find("businessUnitCode", buCode).firstResult();
    return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
  }

  @Override
  public List<Warehouse> search(String location, Integer minCapacity, Integer maxCapacity, String sortBy, String sortOrder, Integer page, Integer pageSize) {
    StringBuilder queryBuilder = new StringBuilder("archivedAt IS NULL");
    java.util.Map<String, Object> params = new java.util.HashMap<>();
    
    if (location != null && !location.isBlank()) {
        queryBuilder.append(" AND location = :location");
        params.put("location", location);
    }
    if (minCapacity != null) {
        queryBuilder.append(" AND capacity >= :minCapacity");
        params.put("minCapacity", minCapacity);
    }
    if (maxCapacity != null) {
        queryBuilder.append(" AND capacity <= :maxCapacity");
        params.put("maxCapacity", maxCapacity);
    }
    
    String sortField = sortBy != null && sortBy.equals("capacity") ? "capacity" : "createdAt";
    String order = sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? "Descending" : "Ascending";
    
    io.quarkus.hibernate.orm.panache.PanacheQuery<DbWarehouse> query = find(queryBuilder.toString(), io.quarkus.panache.common.Sort.by(sortField).direction(io.quarkus.panache.common.Sort.Direction.valueOf(order)), params);
    
    int pageNum = page != null ? page : 0;
    int size = pageSize != null ? pageSize : 10;
    
    return query.page(pageNum, size).list().stream().map(DbWarehouse::toWarehouse).toList();
  }
}
