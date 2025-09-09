package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) throws NoSuchElementException {
        if (carId == null || date == null) return false;
        // TODO: optionally throw NotFound if car does not exist
        carRepository.findById(carId).orElseThrow();
        ResponseEntity.notFound();
        return policyRepository.existsActiveOnDate(carId, date);
    }

    public ResponseEntity<?> registerNewPolicy(Long carId, InsurancePolicy insurance) {
        Car car = carRepository.findById(carId).orElseThrow();
        insurance.setCar(car);

        return ResponseEntity.ok(policyRepository.save(insurance));
    }

}
