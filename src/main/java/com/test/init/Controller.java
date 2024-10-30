package com.test.init;

import com.test.init.entity.Route;
import com.test.init.entity.Vehicle;
import com.test.init.entity.Driver;
import com.test.init.repository.DriverRepo;
import com.test.init.repository.VehicleRepo;
import com.test.init.repository.RouteRepo;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class Controller {
    @Autowired 
    private VehicleRepo vehicleRepo;

    @Autowired 
    private DriverRepo driverRepo;

    @Autowired 
    private RouteRepo routeRepo;

    Logger logger = LoggerFactory.getLogger(Controller.class);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
    
    @GetMapping(value = "/vehicle", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return new ResponseEntity<>(vehicleRepo.findAll(), HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/vehicle", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        vehicleRepo.save(vehicle);
        return new ResponseEntity<>(vehicle, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/driver" , produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return new ResponseEntity<>(driverRepo.findAll(), HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/driver", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        driverRepo.save(driver);
        return new ResponseEntity<>(driver, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/route", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Route>> getAllRoutes() {
        return new ResponseEntity<>(routeRepo.findAll(), HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/route", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createRoute(@RequestBody Route route) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        try {
            LocalDate.parse(route.getStartTime(), formatter);
        } catch (Exception ex) {
            return new ResponseEntity<>("Invalid date offered for startTime, follow format yyyy-MM-dd-HH-mm", HttpStatus.BAD_REQUEST);
        }
        try {
            LocalDate.parse(route.getEndTime(), formatter);
        } catch (Exception ex) {
            return new ResponseEntity<>("Invalid date offered for endTime, follow format yyyy-MM-dd-HH-mm", HttpStatus.BAD_REQUEST);
        }


        routeRepo.save(route);
        return new ResponseEntity<>(route, HttpStatus.ACCEPTED);
    }

    //This should return a List ( 0 - n ) of all drivers on RouteId if Time is null. If Time is not null there should be max one entry in the List
    @GetMapping("/{routeId}/DriverDetails")
    public ResponseEntity getDriversByRoute(@PathVariable Long routeId, @RequestParam(required = false) String time) {
        logger.info("Entering GetDriversByRoute with routeId:" + routeId);
        
        List<Driver> drivers = new ArrayList<>();
        List<Route> routesList = routeRepo.findByRouteId(routeId);

        if (time != null){
            try {
                LocalDate.parse(time, formatter);
            } catch (Exception ex) {
                logger.error("Incorrect date format given", ex);
                return new ResponseEntity<>("Invalid date offered for time, follow format yyyy-MM-dd-HH-mm", HttpStatus.BAD_REQUEST);
            }
            LocalDateTime timeToCheck = LocalDateTime.parse(time, formatter);
            
            for (Route route: routesList){
                LocalDateTime startTime = LocalDateTime.parse(route.getStartTime(), formatter);
                LocalDateTime endTime = LocalDateTime.parse(route.getEndTime(), formatter);

                if (timeToCheck.isBefore(endTime) && timeToCheck.isAfter(startTime)){
                    Optional <Vehicle> optVehicle =  vehicleRepo.findById(route.getVehicleId());
                    if (optVehicle.isPresent()){
                        long driverId = optVehicle.get().getDriverId();
                        Optional<Driver> driver = driverRepo.findById(driverId);
                        if (driver.isPresent()){
                            if (!drivers.contains(driver.get())){
                                drivers.add(driver.get());
                            }
                        }
                    }
                }
            }
        }
        else{
            for (Route route: routesList){
                Optional <Vehicle> optVehicle =  vehicleRepo.findById(route.getVehicleId());
                if (optVehicle.isPresent()){
                    long driverId = optVehicle.get().getDriverId();
                    Optional<Driver> driver = driverRepo.findById(driverId);
                    if (driver.isPresent()){
                        if (!drivers.contains(driver.get())){
                            drivers.add(driver.get());
                        }
                    }
                }
            }
        }

        if (drivers.isEmpty()){
            logger.info("No drivers found");
            return new ResponseEntity<>(drivers, HttpStatus.NOT_FOUND);
        }


        logger.info("Returning list of drivers");
        return new ResponseEntity<>(drivers, HttpStatus.NOT_FOUND);
    }
    
}
