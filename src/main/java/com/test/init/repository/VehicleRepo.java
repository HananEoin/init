package com.test.init.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.init.entity.Vehicle;

public interface VehicleRepo extends JpaRepository <Vehicle, Long>{
    
}
