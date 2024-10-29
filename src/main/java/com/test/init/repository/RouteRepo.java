package com.test.init.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.init.entity.Route;

@Repository
public interface RouteRepo extends JpaRepository <Route, Long>{
    List<Route> findByRouteId(Long routeId);
} 