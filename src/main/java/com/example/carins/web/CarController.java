package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        // TODO: validate date format and handle errors consistently - done
        LocalDate parsedDate = null;

        try {
            parsedDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Date format not valid.");
        }

        boolean valid = false;

        try {
            valid = service.isInsuranceValid(carId, parsedDate);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Car ID was not found.");
        }

        return ResponseEntity.ok(new InsuranceValidityResponse(carId, parsedDate.toString(), valid));
    }

    @PostMapping("/cars/{carId}/policy")
    public ResponseEntity<?> registerNewPolicy(@PathVariable Long carId, @RequestBody InsurancePolicy insurance) {
        service.registerNewPolicy(carId, insurance);

        return ResponseEntity.ok(insurance);
    }

//    @PutMapping("/cars/{carId}/policy")
//    public ResponseEntity<?> updateExistingPolicy() {}

//    @PostMapping("/cars/{carId}/claims")
//    public ResponseEntity<?> registerNewInsuranceClaim(@PathVariable Long carId, @RequestBody , ) {}

    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }

    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}
}
