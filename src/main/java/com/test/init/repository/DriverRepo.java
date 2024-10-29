package com.test.init.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.test.init.entity.Driver;

@Repository
public interface DriverRepo extends JpaRepository <Driver, Long>{
    
    @Query(value = "Select * \n" +
                "FROM DRIVER d, VEHICLE v, ROUTE r \n" + 
                "WHERE d.id=v.driverId AND v.id=r.vehicleId AND r.routeId=REQUESTID ", nativeQuery = true)
    List<Driver> findAllDriversByRoute(@Param("REQUESTID")Long routeId);
}
