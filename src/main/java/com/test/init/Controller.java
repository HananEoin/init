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
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        //Check if start is valid 
        try {
            dateFormat.parse(route.getStartTime());
        } catch (Exception ex) {
            return new ResponseEntity<>("Invalid date offered for startTime, follow format yyyy-MM-dd-HH-mm", HttpStatus.BAD_REQUEST);
        }
        try {
            dateFormat.parse(route.getEndTime());
        } catch (Exception ex) {
            return new ResponseEntity<>("Invalid date offered for endTime, follow format yyyy-MM-dd-HH-mm", HttpStatus.BAD_REQUEST);
        }

        //Check if end is valid time

        routeRepo.save(route);
        return new ResponseEntity<>(route, HttpStatus.ACCEPTED);
    }

    //This should return a List ( 0 - n ) of all drivers on RouteId if Time is null. If Time is not null there should be max one entry in the List
    @GetMapping("/{routeId}/DriverDetails")
    public ResponseEntity GetDriversByRoute(@PathVariable Long routeId, @RequestParam(required = false) String time) {
        List<Driver> drivers = new ArrayList<>();
        List<Route> routesList = routeRepo.findByRouteId(routeId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

        if (time != null){
            try {
                dateFormat.parse(time);
            } catch (Exception ex) {
                return new ResponseEntity<>("Invalid Time offered in Query, follow format yyyy-MM-dd-HH-mm", HttpStatus.BAD_REQUEST);
            }

            for (Route route: routesList){
                if (time.compareTo(route.getEndTime()) > 0 && time.compareTo(route.getStartTime()) < 0){
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
            return new ResponseEntity<>(drivers, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(drivers, HttpStatus.NOT_FOUND);
    }
    
}
